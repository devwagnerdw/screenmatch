package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporadas;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura= new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();

     private ConverteDados conversor= new ConverteDados();

    private final String ENDERECO="https://www.omdbapi.com/?t=";
    private final String API_KEY="&apikey=6002159a";
    public void  exibirMenu(){
        System.out.println("Digite o nome da série para a busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);


        List<DadosTemporadas> temporadas= new ArrayList<>();
		for (int i=1; i<=dados.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season=" + i + API_KEY);
            DadosTemporadas dadosTemporadas= conversor.obterDados(json,DadosTemporadas.class);

			temporadas.add(dadosTemporadas);

		}
		temporadas.forEach(System.out::println);

        for (int i = 0; i < dados.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();

            for (int j = 0; j < episodiosTemporadas.size(); j++) {
                System.out.println(episodiosTemporadas.get(j).titulo());

            }
            System.out.println(episodiosTemporadas.size());
        }



        temporadas.forEach(t->t.episodios().forEach(e -> System.out.println(e.titulo())));


        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);


        List<Episodio>episodios= temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo episódio");
        var trechoTitulo= leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada " + episodioBuscado.get().getTemporada());
        }else {
            System.out.println("Episódio não encontrado!");
        }
//
//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = leitura.nextInt();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador=DateTimeFormatter.ofPattern("dd/MM/yyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episódio: " + e.getTitulo() +
//                                " Data lançamento: " + e.getDataLancamento().format(formatador)
//                ));
    }
}
