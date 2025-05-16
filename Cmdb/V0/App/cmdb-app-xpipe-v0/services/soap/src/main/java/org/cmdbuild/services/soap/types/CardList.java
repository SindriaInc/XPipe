package org.cmdbuild.services.soap.types;

import java.util.ArrayList;
import java.util.List;

public class CardList {

	private int totalRows;
	private List<Card> cards;

	public CardList() {
		cards = new ArrayList<>();
	}

	public CardList(List<Card> cards, int totalRows) {
		this.totalRows = totalRows;
		this.cards = cards;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public void addCard(Card card) {
		this.cards.add(card);
	}
}
