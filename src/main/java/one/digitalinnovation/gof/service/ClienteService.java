package one.digitalinnovation.gof.service;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ClienteService {

    Iterable<Cliente> buscarTodos();
    Cliente buscarPorId(Long id);
    void inserir(Cliente cliente);
    void atualizar(Long id, Cliente cliente);
    void deletar(Long id);

    // NOVOS:
    Page<Cliente> buscarPaginado(Pageable pageable);
    Page<Cliente> buscarPorNome(String nome, Pageable pageable);
    Cliente atualizarParcial(Long id, Map<String, Object> campos);
    List<Cliente> inserirEmLote(List<Cliente> clientes);

    // Endereço / ViaCEP helpers:
    Endereco consultarEnderecoViaCep(String cep);
    Endereco sincronizarEnderecoPorCep(String cep); // busca no ViaCEP e salva no BD
    Endereco buscarEnderecoNoBanco(String cep);
}
