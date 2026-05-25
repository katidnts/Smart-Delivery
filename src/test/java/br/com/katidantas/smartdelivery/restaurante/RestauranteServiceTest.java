package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.CepService;
import br.com.katidantas.smartdelivery.endereco.DadosEnderecoRequestDTO;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.endereco.EnderecoParcialDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestauranteServiceTest {

    @Mock
    private RestauranteRepository repository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private RestauranteService restauranteService;

    @Test
    @DisplayName("Deve salvar e retornar o restaurante quando todos os dados estiverem corretos")
    void deveSalvarRestaurante_QuandoTodosOsDadosEstiveremCorretos() {

        //Given

        DadosEnderecoRequestDTO enderecoRequestDTO = criaDadosEnderecoRequestDTOMock();

        Restaurante restaurante = new Restaurante(1L, "Casa do drink", "999999999", "11999999999", enderecoRequestDTO.toEntity(), true);
        EnderecoParcialDTO enderecoParcialDTO = new EnderecoParcialDTO(
                "22220001",
                "Rua do Catete",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        );

        when(cepService.buscarCep("22220001")).thenReturn(enderecoParcialDTO);
        when(repository.save(any())).thenReturn(restaurante);

        //When

        Restaurante resultado = restauranteService.save(restaurante);

        //Then

        assertThat(resultado.getEndereco().getLogradouro()).isEqualTo("Rua do Catete");
        assertThat(resultado.getEndereco().getBairro()).isEqualTo("Catete");
        assertThat(resultado.getEndereco().getCidade()).isEqualTo("Rio de Janeiro");
        assertThat(resultado.getEndereco().getUf()).isEqualTo("RJ");

        verify(repository).save(restaurante);

    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando CEP nulo ou com formato inválido")
    void deveLancarIllegalArgumentException_QuandoCepNuloOuFormatoInvalido() {

        //Given
        Restaurante restaurante = criaRestauranteMock();
        restaurante.getEndereco().setCep("2222000");

        when(cepService.buscarCep("2222000")).thenThrow(new IllegalArgumentException("O CEP deve conter 8 dígitos"));

        //Then

        assertThatThrownBy(() -> restauranteService.save(restaurante))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O CEP deve conter 8 dígitos");

        verify(cepService).buscarCep("2222000");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando CEP não existir na API")
    void deveLancarIllegalArgumentException_QuandoCepNaoExistirNaApi() {

        //Given

        Restaurante restaurante = criaRestauranteMock();
        restaurante.getEndereco().setCep("99999999");

        when(cepService.buscarCep("99999999")).thenThrow(new IllegalArgumentException("CEP inválido!"));

        //Then

        assertThatThrownBy(() -> restauranteService.save(restaurante))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CEP inválido!");

        verify(cepService).buscarCep("99999999");

    }

    @Test
    @DisplayName("Deve buscar um restaurante por ID quando o ID for válido")
    void deveBuscarRestaurante_QuandoIdValido() {

        //Given

        DadosEnderecoRequestDTO enderecoRequestDTO = criaDadosEnderecoRequestDTOMock();

        Restaurante restaurante = new Restaurante(1L, "Casa do drink", "999999999", "11999999999", enderecoRequestDTO.toEntity(), true);

        when(repository.findByIdAndAtivoEquals(1L, true)).thenReturn(Optional.of(restaurante));

        //When

        Restaurante resultado = restauranteService.buscarRestaurantePorId(1L);

        //Then

        assertThat(resultado.getId()).isEqualTo(restaurante.getId());
        assertThat(resultado.getNome()).isEqualTo(restaurante.getNome());
        assertThat(resultado.getTelefone()).isEqualTo(restaurante.getTelefone());
        assertThat(resultado.getCnpj()).isEqualTo(restaurante.getCnpj());
        assertThat(resultado.getEndereco().getCep()).isEqualTo(enderecoRequestDTO.cep());
        assertThat(resultado.getEndereco().getNumero()).isEqualTo(enderecoRequestDTO.numero());
        assertThat(resultado.getEndereco().getComplemento()).isEqualTo(enderecoRequestDTO.complemento());
        assertThat(resultado.getAtivo()).isEqualTo(restaurante.getAtivo());

        verify(repository).findByIdAndAtivoEquals(1L, true);

    }

    @Test
    @DisplayName("Deve listar todos os restaurantes ativos")
    void deveListarRestaurantes_QuandoAtivos() {

        //Given

        List<Restaurante> restaurantes = criaListaRestauranteMock();
        Page<Restaurante> restaurantesMock = new PageImpl<>(restaurantes);

        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(restaurantesMock);

        //When

        var resultado = restauranteService.listarRestaurantes(restaurantesMock.getPageable());

        //Then

        assertThat(resultado.getContent()).containsExactlyElementsOf(restaurantes);
        assertThat(resultado.getTotalElements()).isEqualTo(restaurantes.size());

        verify(repository).findAllByAtivoTrue(restaurantesMock.getPageable());

    }

    @Test
    @DisplayName("Deve atualizar os campos do restaurante quando os dados forem válidos")
    void deveAtualizarCampos_QuandoDadosValidos() {

        //Given

        Restaurante restaurante = criaRestauranteMock();

        when(repository.findByIdAndAtivoEquals(restaurante.getId(), true)).thenReturn(Optional.of(restaurante));

        //when

        var resultado = restauranteService.atualizarCampos(restaurante.getId(), restaurante);

        //Then

        assertThat(resultado.getNome()).isEqualTo(restaurante.getNome());
        assertThat(resultado.getTelefone()).isEqualTo(restaurante.getTelefone());
        assertThat(resultado.getCnpj()).isEqualTo(restaurante.getCnpj());
        assertThat(resultado.getEndereco()).isEqualTo(restaurante.getEndereco());

        verify(repository).findByIdAndAtivoEquals(restaurante.getId(), true);

    }

    @Test
    @DisplayName("Deve retornar restaurante sem alteração quando todos os campos de restauranteAtualizado são null")
    void deveRetornarRestaurante_QuandoTodosOsCamposSaoNull() {

        //Given

        Restaurante restauranteExistente = criaRestauranteMock();
        Restaurante restauranteAtualizado = new Restaurante();

        when(repository.findByIdAndAtivoEquals(1L, true)).thenReturn(Optional.of(restauranteExistente));

        //When

        var resultado = restauranteService.atualizarCampos(1L, restauranteAtualizado);

        //Then

        assertThat(resultado.getNome()).isEqualTo(restauranteExistente.getNome());
        assertThat(resultado.getTelefone()).isEqualTo(restauranteExistente.getTelefone());
        assertThat(resultado.getEndereco()).isEqualTo(restauranteExistente.getEndereco());

        verify(repository).findByIdAndAtivoEquals(1L, true);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar atualizar restaurante com ID não encontrado")
    void deveLancarEntityNotFoundException_AoAtualizarRestauranteComIdNaoEncontrado() {

        //Given
        Restaurante restaurante = criaRestauranteMock();

        when(repository.findByIdAndAtivoEquals(2L, true)).thenReturn(Optional.empty());

        //Then

        assertThatThrownBy(() -> restauranteService.atualizarCampos(2L, restaurante))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("O Restaurante com o id informado: 2 não existe!");

        verify(repository).findByIdAndAtivoEquals(2L, true);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar atualizar restaurante inativo")
    void deveLancarEntityNotFoundException_AoAtualizarRestauranteInativo() {

        //Given
        Restaurante restaurante = criaRestauranteMock();
        restaurante.setAtivo(false);

        when(repository.findByIdAndAtivoEquals(1L, true)).thenReturn(Optional.empty());

        //Then

        assertThatThrownBy(() -> restauranteService.atualizarCampos(1L, restaurante))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("O Restaurante com o id informado: 1 não existe!");

        verify(repository).findByIdAndAtivoEquals(1L, true);
    }

    @Test
    @DisplayName("Deve inativar restaurante quando ativo")
    void deveInativarRestaurante_QuandoAtivo() {

        //Given

        Restaurante restaurante = criaRestauranteMock();

        when(repository.findByIdAndAtivoEquals(1L, true)).thenReturn(Optional.of(restaurante));
        when(repository.save(restaurante)).thenReturn(restaurante);

        //When

        var resultado = restauranteService.inativar(1L);

        //Then

        assertThat(resultado.getNome()).isEqualTo(restaurante.getNome());
        assertThat(resultado.getCnpj()).isEqualTo(restaurante.getCnpj());
        assertThat(resultado.getTelefone()).isEqualTo(restaurante.getTelefone());
        assertThat(resultado.getAtivo()).isFalse();
        assertThat(resultado.getEndereco()).isEqualTo(restaurante.getEndereco());

        verify(repository).findByIdAndAtivoEquals(1L, true);
        verify(repository).save(restaurante);

    }

    private static DadosEnderecoRequestDTO criaDadosEnderecoRequestDTOMock() {
        return new DadosEnderecoRequestDTO(
                "22220001",
                "123",
                "500"
        );

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
                "RJ"
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
                "RJ"
        ));
        return List.of(restaurante1, restaurante2);

    }

    private Restaurante criaRestauranteMock() {

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
        entidade.setNome("The new coffee");
        entidade.setTelefone("999998888");
        entidade.setCnpj("11222333000181");
        entidade.setEndereco(endereco);
        entidade.setAtivo(true);

        return entidade;

    }

}
