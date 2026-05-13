package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoRequestDTO;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestauranteController.class)
@ActiveProfiles("test")
public class RestauranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestauranteService restauranteService;


    @Test
    void deveCadastrarRestaurante_quandoDadosValidos() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();
        DadosRestauranteDTO dtoValido = criaDTOCadastro();

        when(restauranteService.save(any())).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteService).save(restauranteArgumentCaptor.capture());

        assertDtoCadastroCorrespondeEntidade(dtoValido, restauranteArgumentCaptor.getValue());

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTO = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        assertDtoCorrespondeEntidade(dadosDetalhamentoRestauranteDTO, restauranteMock);
    }

    @Test
    void deveBuscarRestaurante_QuandoIdValido() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();
        Long id = 1L;
        when(restauranteService.buscarRestaurantePorId(id)).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(get("/restaurantes/{id}", id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(restauranteService).buscarRestaurantePorId(id);

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTO = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        assertDtoCorrespondeEntidade(dadosDetalhamentoRestauranteDTO, restauranteMock);

    }

    @Test
    void deveBuscarListaDeRestaurantesAtivos() throws Exception {

        List<Restaurante> restaurantesMock = criaListaRestauranteMock();
        Page<Restaurante> page = new PageImpl<>(restaurantesMock);

        when(restauranteService.listarRestaurantes(any(Pageable.class))).thenReturn(page);

        String responseBody = mockMvc.perform(get("/restaurantes"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(restauranteService).listarRestaurantes(any(Pageable.class));

        RestaurantePage dto = objectMapper.readValue(responseBody, RestaurantePage.class);

        assertListaCorrespondeEntidades(dto.getContent(), restaurantesMock);

    }

    @Test
    void deveAtualizarRestaurante() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();
        DadosAtualizacaoRestauranteDTO dtoValido = criaDTOAtualizacao();

        Long id = 1L;
        when(restauranteService.atualizarCampos(eq(id), any())).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(patch("/restaurantes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido))

                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteService).atualizarCampos(idArgumentCaptor.capture(), restauranteArgumentCaptor.capture());

        assertDtoAtualizacaoCorrespondeEntidade(dtoValido, restauranteArgumentCaptor.getValue());

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTOMock = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        assertDtoCorrespondeEntidade(dadosDetalhamentoRestauranteDTOMock, restauranteMock);

    }

    @Test
    void deveDeletarRestauranteQuandoIdValido() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();
        Long id = restauranteMock.getId();

        when(restauranteService.inativar(id)).thenReturn(restauranteMock);

        mockMvc.perform(delete("/restaurantes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(restauranteService).inativar(idArgumentCaptor.capture());
        assertThat(idArgumentCaptor.getValue()).isEqualTo(id);
    }


    public Restaurante criaRestauranteMock() {

        Endereco endereco = new Endereco();
        endereco.setCep("22220001");
        endereco.setLogradouro("Rua do Catete");
        endereco.setNumero("52");
        endereco.setComplemento("902");
        endereco.setBairro("Catete");
        endereco.setCidade("Rio de janeiro");
        endereco.setUf("RJ");

        Restaurante entidade = new Restaurante();
        entidade.setId(1L);
        entidade.setNome("The best coffee");
        entidade.setTelefone("999999999");
        entidade.setCnpj("11222333000181");
        entidade.setEndereco(endereco);
        entidade.setAtivo(true);

        return entidade;
    }

    private DadosRestauranteDTO criaDTOCadastro() {
        DadosEnderecoRequestDTO enderecoRequestDTO = new DadosEnderecoRequestDTO(
                "22220001",
                "52",
                "902"
        );

        return new DadosRestauranteDTO(
                "The best coffee",
                "999999999",
                "11222333000181",
                enderecoRequestDTO
        );
    }

    private void assertDtoCadastroCorrespondeEntidade(DadosRestauranteDTO dtoValido, Restaurante restaurante) {

        assertThat(restaurante.getNome()).isEqualTo(dtoValido.nome());
        assertThat(restaurante.getTelefone()).isEqualTo(dtoValido.telefone());
        assertThat(restaurante.getCnpj()).isEqualTo(dtoValido.cnpj().replaceAll("\\D", ""));
        assertThat(restaurante.getEndereco()).isNotNull();
    }

    private List<Restaurante> criaListaRestauranteMock() {
        var restaurante1 = new Restaurante();
        restaurante1.setAtivo(true);
        restaurante1.setNome("Casa da feijoada");
        restaurante1.setCnpj("34665790000109");
        restaurante1.setTelefone("888888888");
        restaurante1.setEndereco(new Endereco(
                1L,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "Uf"
        ));

        var restaurante2 = new Restaurante();
        restaurante2.setAtivo(true);
        restaurante2.setNome("Casa do Sushi");
        restaurante2.setCnpj("07526557000100");
        restaurante2.setTelefone("222222222");
        restaurante2.setEndereco(new Endereco(
                1L,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "Uf"
        ));
        return List.of(restaurante1, restaurante2);
    }

    private void assertListaCorrespondeEntidades(List<DadosListaRestauranteDTO> dtos, List<Restaurante> restaurantes) {
        assertThat(dtos).hasSize(restaurantes.size());
        for (int i = 0; i < dtos.size(); i++) {
            assertDtoListaCorrespondeEntidade(dtos.get(i), restaurantes.get(i));
        }
    }

    private void assertDtoListaCorrespondeEntidade(DadosListaRestauranteDTO dto, Restaurante restaurante) {
        assertThat(dto.nome()).isEqualTo(restaurante.getNome());
        assertThat(dto.telefone()).isEqualTo(restaurante.getTelefone());
        assertThat(dto.endereco()).isNotNull();
    }

    public DadosAtualizacaoRestauranteDTO criaDTOAtualizacao() {
        DadosAtualizacaoRestauranteDTO dtoValido = new DadosAtualizacaoRestauranteDTO(
                1L,
                "Café atualizado",
                "888888888",
                null
        );
        return dtoValido;
    }

    private void assertDtoCorrespondeEntidade(DadosDetalhamentoRestauranteDTO dto, Restaurante entidade) {

        assertThat(dto.id()).isEqualTo(entidade.getId());
        assertThat(dto.nome()).isEqualTo(entidade.getNome());
        assertThat(dto.telefone()).isEqualTo(entidade.getTelefone());
        assertThat(dto.cnpj()).isEqualTo(entidade.getCnpj());
        assertThat(dto.endereco().cep()).isEqualTo(entidade.getEndereco().getCep());
        assertThat(dto.endereco().logradouro()).isEqualTo(entidade.getEndereco().getLogradouro());
        assertThat(dto.endereco().numero()).isEqualTo(entidade.getEndereco().getNumero());
        assertThat(dto.endereco().complemento()).isEqualTo(entidade.getEndereco().getComplemento());
        assertThat(dto.endereco().bairro()).isEqualTo(entidade.getEndereco().getBairro());
        assertThat(dto.endereco().localidade()).isEqualTo(entidade.getEndereco().getCidade());
        assertThat(dto.endereco().uf()).isEqualTo(entidade.getEndereco().getUf());
    }

    private void assertDtoAtualizacaoCorrespondeEntidade(DadosAtualizacaoRestauranteDTO dtoValido, Restaurante restaurante) {

        assertThat(restaurante.getNome()).isEqualTo(dtoValido.nome());
        assertThat(restaurante.getTelefone()).isEqualTo(dtoValido.telefone());

    }
}



