package utils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkAlgorithm<T> {
    private final ForkJoinPool pool = new ForkJoinPool(); //gestisce i Task da me Creati

    public String print(List<T> args) {
        // Crea un task per sommare gli elementi dell'array
        PrintTask task = new PrintTask(args, 0, args.size());
        return (String) pool.invoke(task);   //invoca la funzione ricorsiva
    }

    private class PrintTask extends RecursiveTask<T> {    //estende la Classe che permette la ricorsione

        private final List<T> list;   //lista di obj da stampare
        private final int start;  //inizio
        private final int end;   //lunghezza massima

        // Costruttore che prende l'array, l'indice di inizio e fine
        public PrintTask(List<T> list, int start, int end) {  //parametri all'apparenza futili,ma servono per la ricorsione
            Objects.requireNonNull(list);
            this.list = list;
            this.start = start;
            this.end = end;
        }

        @Override
        protected T compute() {

            if (end - start <= 2) {  // "ritaglia" la lista ed estrae i valori
                StringBuilder r = new StringBuilder();
                for (int i = start; i < end; i++) {
                    r.append(list.get(i)).append("\n");  //appende i valori dlela lista
                }
                return (T) r.toString();
            } else {

                // Calcolare il punto medio correttamente
                int mid = start + (end - start) / 2;
                PrintTask task1 = new PrintTask(list, start, mid);
                PrintTask task2 = new PrintTask(list, mid, end);

                // Fork i due task e attendi i risultati
                task1.fork();
                task2.fork();

                // Unisci i risultati e ritorno
                return (T) ((String) task1.join() + task2.join());
            }
        }
    }
}
