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
		// Evita que o m�todo seja executado varias vezes. Se tirar ele entre em loop.
		if (isUpdating) {
			isUpdating = false;
			return;
		}

		isUpdating = true;
		String str = s.toString();
		// Verifica se j� existe a m�scara no texto.
		boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) && (str.indexOf(".") > -1 || str.indexOf(",") > -1));

		// Verifica se existe m�scara.
		if (hasMask) {
			// Retira a m�scara.
			str = str.replaceAll("[R$]", "").replaceAll("[,]", "").replaceAll("[.]", "");
		}

		try {
			// Transforma o n�mero que est� escrito no EditText em monet�rio.
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
