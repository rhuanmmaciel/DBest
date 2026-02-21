package ibd.transaction.concurrency.locktable.items;

import java.util.ArrayList;

import ibd.transaction.concurrency.Item;

public class GabrielLuizGarbossaItemCollection implements ItemCollection {
	
	// A implementação da Item Collection se dá através de uma Árvore balanceada de Intervalos
	// em que cada nó da árvore possui, além dos limites superior e inferior, o maior valor
	// da suas sub-árvores
	
	static class No { // Estrutura do nó utilizado na árvore
		Item intervalo;
		long max;
		No esq, dir;
		int altura;
		
		public No(Item intervalo) {
			this.intervalo = intervalo;
			this.max = intervalo.getHigher();
			this.altura = 1;
			this.esq = this.dir = null;
		}
	}
	
	No items;	// Raiz da ItemCollection implementada
	
	private int retornaAltura(No n) {
		if (n==null) {
			return 0;
		}
		
		return n.altura;
	}
	
	private long retornaMax(No n) {
		if (n==null) {
			return 0;
		}
		return n.max;
	}
	
	private int retornaBalanceamento(No n) {
		if (n==null) {
			return 0;
		}
		return retornaAltura(n.esq) - retornaAltura(n.dir);
	}
	
	private int max(int a, int b) { // Retorna o maior entre dois inteiros
		if (a > b) {
			return a;
		}
		return b;
	}
	
	private long max(long a, long b) { // Retorna o maior entre dois longs
		if (a > b) {
			return a;
		}
		return b;
	}
	
	private No rotacionaParaDireita(No n) { // Realiza uma rotação na árvore para a direita
		No esq = n.esq;
		No dir = esq.dir;
		
		// Rotação
		esq.dir = n;
		n.esq = dir;
		
		// Atualizando as alturas
		n.altura = max(retornaAltura(n.esq),retornaAltura(n.dir)) + 1;
		esq.altura = max(retornaAltura(esq.esq), retornaAltura(esq.dir)) + 1;
		
		// Atualizando os valores máximos
		n.max = max(n.intervalo.getHigher(),max(retornaMax(n.esq), retornaMax(n.dir)));
		esq.max = max(esq.intervalo.getHigher(),max(retornaMax(esq.esq),retornaMax(esq.dir)));
		
		// Retorna nova raiz
		return esq;
	}
	
	private No rotacionaParaEsquerda(No n) { // Realiza uma rotação na árvore para a esquerda
		No dir = n.dir;
		No esq = dir.esq;
		
		// Rotação
		dir.esq = n;
		n.dir = esq;
		
		// Atualizando as alturas
		n.altura = max(retornaAltura(n.esq),retornaAltura(n.dir)) + 1;
		dir.altura = max(retornaAltura(dir.esq), retornaAltura(dir.dir)) + 1;
		
		// Atualizando os valores máximos
		n.max = max(n.intervalo.getHigher(),max(retornaMax(n.esq), retornaMax(n.dir)));
		dir.max = max(dir.intervalo.getHigher(),max(retornaMax(dir.esq),retornaMax(dir.dir)));
		
		// Retorna nova raiz
		return dir;
	}
	
	private No insereNo(No raiz, Item intervalo) { // Insere um novo nó na árvore
		No n = new No(intervalo);
		
		if (raiz==null) { // Se a arvore estiver vazia
			return n;
		}
		
		long i = raiz.intervalo.getLower();
		if (intervalo.getLower() < i) {
			raiz.esq = insereNo(raiz.esq, intervalo); // Se o inicio do intervalo for menor que o inicio da raiz, o novo nó vai pra o filho esquerdo
		} else {
			raiz.dir = insereNo(raiz.dir, intervalo); // Se o inicio do intervalo for maior que o inicio da raiz, o novo nó vai pra o filho direito
		}
		
		raiz.altura = 1 + max(retornaAltura(raiz.esq),retornaAltura(raiz.dir)); // Atualiza a altua da raiz
		
		int balanceamento = retornaBalanceamento(raiz); // Checa o balanceamento da raiz
		
		if (balanceamento > 1 && intervalo.getLower() < raiz.esq.intervalo.getLower()) {
			// Rotação simples para a direita
			return rotacionaParaDireita(raiz);
		}
		if (balanceamento < -1 && intervalo.getLower() > raiz.dir.intervalo.getLower()) {
			// Rotação simples para a esquerda
			return rotacionaParaEsquerda(raiz);
		}
		if (balanceamento > 1 && intervalo.getLower() > raiz.esq.intervalo.getLower()) {
			// Rotação para a esquerda e então para a direita
			raiz.esq = rotacionaParaEsquerda(raiz.esq);
			return rotacionaParaDireita(raiz);
		}
		if (balanceamento < -1 && intervalo.getLower() < raiz.dir.intervalo.getLower()) {
			// Rotação para a direita e então para a esquerda
			raiz.dir = rotacionaParaDireita(raiz.dir);
			return rotacionaParaEsquerda(raiz);
		}
		
		
		return raiz; // Caso nenhuma alteração ocorra, retorna a raiz inalterada
	}
	
	private No retornaNo(No raiz, long l, long h) { // Retorna um nó com u mdeterminado intervalo
		if (raiz == null) { // Se o nó atual for nulo, chegamos ao fim da árvore
			return null;
		}
		Item i = raiz.intervalo;
		if (l == i.getLower() && h == i.getHigher()) { // Se o nó procurado for o nó atual
			return raiz;
		} else {
			if (l < i.getLower()) {
				return retornaNo(raiz.esq,l,h); // Se o intervalo inferior procurado for menor do que o do nó atual, continuamos a busca no filho esquerdo
			} else {
				return retornaNo(raiz.dir,l,h); // Do contrário, continuamo a busca no filho direito
			}
		}
	}
	
	private void percorreArvore(No raiz, ArrayList<Item> c) { // Armazena todos os valores presentes na árvore
		if (raiz == null) return; // Fim da árvore
		
		c.add(raiz.intervalo);
		
		percorreArvore(raiz.esq, c);
		percorreArvore(raiz.dir, c);
	}
	
	private void procuraSobreposicao(No raiz, long lower, long higher, ArrayList<Item> c) { // Armazena sobreposições encontradas
		MainItem.steps++;
                if (raiz==null) return;
		
		Item item = raiz.intervalo;
		
		if (item.getLower() <= higher && item.getHigher() >= lower) { // Se ocorrer a sobreposição no nó atual, adiciona ao resultado
			c.add(item);
			// Caso intersecte com a raiz precisamos checar individualmente se intersecta com as sub-arvores
			procuraSobreposicao(raiz.esq, lower,higher,c);
			procuraSobreposicao(raiz.dir, lower,higher,c);
		} // Caso contrário, o intervalo desejada só pode intersectar com a árvore da esquerda OU da direita
		else if (raiz.esq != null && raiz.esq.max > lower) {
			procuraSobreposicao(raiz.esq,lower,higher,c); // So o valor máximo da sub-arvore esquerda for maior que o limite inferior, a sobreposição ainda pode ocorrer
		} else if (raiz.dir != null && raiz.dir.intervalo.getLower() < higher) {
			procuraSobreposicao(raiz.dir,lower,higher,c); // Se o limite inferior do nó direito for menor que o limite superior, a sobreposição ainda pode ocorrer
		}
		
	}
	
	@Override
	public Item addItem(long lower, long higher) {
		Item item = getItem(lower,higher);
		if (item==null) {
			item = new Item(lower,higher);
			items = insereNo(items,item);
		}
		return item;
	}
	
	@Override
	public Item getItem(long lower, long higher) {
		No n = retornaNo(items,lower,higher);
		
		if (n==null) return null;
		
		return n.intervalo;
	}
	
	@Override
	public Iterable<Item> getAllItems() {
		ArrayList<Item> saida = new ArrayList<>();
		
		percorreArvore(items,saida);
		
		return saida;
	}
	
	@Override
	public Iterable<Item> getOverlappedItems(long lower, long higher) {
		ArrayList<Item> saida = new ArrayList<>();
		
		procuraSobreposicao(items,lower,higher,saida);
		
		return saida;
	}
}
