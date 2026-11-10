package com.cris.comprebem.service;

import com.cris.comprebem.entity.Produto;
import com.cris.comprebem.entity.dto.CriarProdutoDTO;
import com.cris.comprebem.entity.dto.ProdutoResponseDTO;
import com.cris.comprebem.exception.EntidadeNotFound;
import com.cris.comprebem.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    @Transactional
    public ProdutoResponseDTO criar(CriarProdutoDTO produtoDTO) {
        Produto produto = new Produto();

        produto.setNome(produtoDTO.nome());
        produto.setPreco(BigDecimal.valueOf(produtoDTO.preco()));
        produto.setDescricao(produtoDTO.descricao());
        produto.setCategoria(produtoDTO.categoria());
        produto.setQuantidade(produtoDTO.quantidade());

        produto = produtoRepository.save(produto);

        produto.setImagem("https://picsum.photos/seed/" + produto.getId() + "/200");

        return new ProdutoResponseDTO(produto);
    }

    public Page<ProdutoResponseDTO> listarTodos(Pageable pageable) {
        var produtos = produtoRepository.findAll(pageable);
        return produtos.map(ProdutoResponseDTO::new);
    }

    public ProdutoResponseDTO buscar(Long id) {
        Produto produto = buscarPorId(id);
        return new ProdutoResponseDTO(produto);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(
                () -> new EntidadeNotFound("Produto não encontrado")
        );
    }

    public Page<ProdutoResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return produtoRepository.findAllByNomeContainingIgnoreCase(nome, pageable)
                .map(ProdutoResponseDTO::new);
    }

    public ProdutoResponseDTO atualizar(Long id, CriarProdutoDTO produto) {
        Produto produtoExistente = buscarPorId(id);

        produtoExistente.setNome(produto.nome());
        produtoExistente.setPreco(BigDecimal.valueOf(produto.preco()));
        produtoExistente.setDescricao(produto.descricao());
        produtoExistente.setCategoria(produto.categoria());
        produtoExistente.setQuantidade(produto.quantidade());

        return new ProdutoResponseDTO(produtoRepository.save(produtoExistente));
    }

    public void deletar(Long id) {
        Produto produtoExistente = buscarPorId(id);
        produtoRepository.delete(produtoExistente);
    }
}
