package one.digitalinnovation.gof.service.impl;

import java.util.Optional;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		// Buscar todos os Clientes.
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Buscar Cliente por ID.
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		// Buscar Cliente por ID, caso exista:
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if (clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
		}
	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID.
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}

	 @Override
    public Page<Cliente> buscarPaginado(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Page<Cliente> buscarPorNome(String nome, Pageable pageable) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    @Override
    public Cliente atualizarParcial(Long id, Map<String, Object> campos) {
        Cliente existente = buscarPorId(id);
        // Campos simples; se quiser algo mais robusto, use BeanUtils ou Jackson mixin
        if (campos.containsKey("nome")) {
            existente.setNome(String.valueOf(campos.get("nome")));
        }
        if (campos.containsKey("endereco")) {
            // Espera um mapa com pelo menos "cep"
            @SuppressWarnings("unchecked")
            Map<String, Object> end = (Map<String, Object>) campos.get("endereco");
            if (end != null && end.get("cep") != null) {
                String cep = String.valueOf(end.get("cep"));
                Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
                    Endereco novo = viaCepService.consultarCep(cep);
                    enderecoRepository.save(novo);
                    return novo;
                });
                existente.setEndereco(endereco);
            }
        }
        return clienteRepository.save(existente);
    }

    @Override
    public List<Cliente> inserirEmLote(List<Cliente> clientes) {
        for (Cliente c : clientes) {
            String cep = c.getEndereco().getCep();
            Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
                Endereco novo = viaCepService.consultarCep(cep);
                enderecoRepository.save(novo);
                return novo;
            });
            c.setEndereco(endereco);
        }
        return (List<Cliente>) clienteRepository.saveAll(clientes);
    }

    @Override
    public Endereco consultarEnderecoViaCep(String cep) {
        return viaCepService.consultarCep(cep);
    }

    @Override
    public Endereco sincronizarEnderecoPorCep(String cep) {
        // Busca no ViaCEP e salva/atualiza no BD
        Endereco novo = viaCepService.consultarCep(cep);
        enderecoRepository.save(novo);
        return novo;
    }

    @Override
    public Endereco buscarEnderecoNoBanco(String cep) {
        return enderecoRepository.findById(cep).orElse(null);
    }

}
