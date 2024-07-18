import java.util.Random;
import java.util.concurrent.Semaphore;

class Usuario extends Thread {
    private int id;
    private Livro[] livros;
    private Random r;

    public Usuario(int id, Livro[] livros) {
        this.id = id;
        this.livros = livros;
        r = new Random();
    }

    public void run() {
        while (true) {
            try {
                // O usuário deve escolher aleatoriamente um livro dentre os 10 livros disponíveis
                Livro livro = pegarLivroAleatorio();
                // Caso o livro esteja atualmente emprestado, o usuário deve esperar até o livro escolhido estar disponível novamente e então ir ao próximo passo
                while (!livro.emprestar()) {
                    System.out.println("Usuário " + this.id + " está esperando o " + livro);
                    livro.aguardarDisponibilidade();
                }
                // Caso o livro esteja disponível, o usuário deve emprestar o livro e permanecer com ele por um tempo aleatório
                System.out.println("Usuário " + this.id + " emprestou o " + livro);
                esperar();
                // Devolver o livro
                livro.devolver();
                System.out.println("Usuário " + this.id + " devolveu o " + livro);
                esperar();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Livro pegarLivroAleatorio() {
        int indiceLivro = r.nextInt(livros.length);
        Livro livro = this.livros[indiceLivro];
        return livro;
    }

    public void esperar() {
        try {
            int tempo = r.nextInt(1000) + 1000; // Gera um número aleatório entre 1000 e 2000 (inclusive)
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Livro extends Semaphore {
    private int id;

    public Livro(int id) {
        super(1);
        this.id = id;
    }

    public String toString() {
        return "livro_" + this.id;
    }

    public boolean emprestar() {
        return this.tryAcquire();
    }

    public void devolver() {
        this.release();
    }

    public void aguardarDisponibilidade() throws InterruptedException {
        if (!this.tryAcquire()) {
            this.acquire();
            this.release();
        }
    }
}

public class TDE {
    public static void main(String[] args) {
        Livro[] livros = new Livro[10];
        Usuario[] usuarios = new Usuario[3];
        for (int i = 0; i < livros.length; i++) {
            livros[i] = new Livro(i);
        }
        for (int i = 0; i < usuarios.length; i++) {
            usuarios[i] = new Usuario(i, livros);
            usuarios[i].start();
        }
    }
}
