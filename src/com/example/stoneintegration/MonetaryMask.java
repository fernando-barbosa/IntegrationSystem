package com.example.stoneintegration;

import java.text.NumberFormat;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MonetaryMask implements TextWatcher {

	final EditText valueEditText;

	public MonetaryMask(EditText valorEditText) {
		super();
		this.valueEditText = valorEditText;
	}

	private boolean isUpdating = false;
	// Pega a formatacao do sistema, se for brasil R$ se EUA US$
	private NumberFormat nf = NumberFormat.getCurrencyInstance();

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int after) {
		// Evita que o método seja executado varias vezes. Se tirar ele entre em loop.
		if (isUpdating) {
			isUpdating = false;
			return;
		}

		isUpdating = true;
		String str = s.toString();
		// Verifica se já existe a máscara no texto.
		boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) && (str.indexOf(".") > -1 || str.indexOf(",") > -1));

		// Verifica se existe máscara.
		if (hasMask) {
			// Retira a máscara.
			str = str.replaceAll("[R$]", "").replaceAll("[,]", "").replaceAll("[.]", "");
		}

		try {
			// Transforma o número que está escrito no EditText em monetário.
			str = nf.format(Double.parseDouble(str) / 100);
			valueEditText.setText(str);
			valueEditText.setSelection(valueEditText.getText().length());
		} catch (NumberFormatException e) {
			s = "";
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}  
	
}
