package com.example.stoneintegration;

import br.com.stone.classes.StartTransaction;
import br.com.stone.methods.TransactionResponse;
import br.com.stone.objects.Transaction;
import br.com.stone.xml.ReturnOfTransactionXml;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnCheckedChangeListener, View.OnClickListener {
	
	private static final int REQUEST_ENABLE_BT = 0;
	BluetoothAdapter mBluetoothAdapter;
	
	public EditText valueEditText;
	
	public Spinner spinner;
	
	public RadioGroup tipoCompraRadioGroup; 
	public RadioGroup parcelaRadioGroup;
	
	public RadioButton debitoRadioButton;
	public RadioButton creditoRadioButton;
	public RadioButton aVistaRadioButton;
	public RadioButton parceladoRadioButton;
	
	public Button button;
	
	Bundle backActivity = null;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        valueEditText = (EditText)findViewById(R.id.valueEditText);
        valueEditText.addTextChangedListener(new MonetaryMask(valueEditText));
        
        spinner = (Spinner) findViewById(R.id.parcelamentoSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.pacelamento_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        
        tipoCompraRadioGroup = (RadioGroup) findViewById(R.id.tipoCompraRadioGroup);
        tipoCompraRadioGroup.setOnCheckedChangeListener(this);
        parcelaRadioGroup = (RadioGroup) findViewById(R.id.parcelaRadioGroup);     
        parcelaRadioGroup.setOnCheckedChangeListener(this);
        
        debitoRadioButton = (RadioButton) findViewById(R.id.debitoRadioButton);
    	creditoRadioButton = (RadioButton) findViewById(R.id.creditoRadioButton);
    	aVistaRadioButton = (RadioButton) findViewById(R.id.aVistaRadioButton);
    	parceladoRadioButton = (RadioButton) findViewById(R.id.parceladoRadioButton);
    	
    	button = (Button) findViewById(R.id.resultButton);
    	button.setOnClickListener(this);
    	
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    protected void onResume() {
    	super.onResume();
    	
    	// Verifica se o Bluetooth está ativado, se não estiver ativa.
    	if (mBluetoothAdapter.isEnabled() == false) {
    		Intent enableBtIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    	
    	responseTransaction();
    }

	// Cria um objeto transação e o popula com os dados preenchidos no app de integração para enviar ao app da Stone. 
    private void startTransaction() {
    	Transaction mTransaction = new Transaction();
		mTransaction.setAmount(valueEditText.getText().toString());
		mTransaction.setTypeOfPurchase(debitoRadioButton.isChecked() ? 1 : 2);
		mTransaction.setTypeOfInstalment(aVistaRadioButton.isChecked() ? 0 : 1);
		mTransaction.setNumberOfInstalments(Integer.valueOf(spinner.getSelectedItem().toString()));
		mTransaction.setDemandId(0);
		
		StartTransaction.startNewTransaction(this, mTransaction, null, null);
	}

    // Ao clicar no botão os dados são enviados para o app da Stone.
	public void onClick(View v) {
    	if (valueEditText.getText().toString().equals("") == false) {
			startTransaction();
		} else {
			Toast.makeText(getApplicationContext(), "Valores precisam estar preenchidos.", Toast.LENGTH_SHORT).show();
		}
	}
    
    // Pega os dados do app da Stone através de um bundle e cria um log informativo. 
    private void responseTransaction() {
    	backActivity = getIntent().getExtras();
    	
    	if (backActivity != null) {
    		
    		String xmlTransaction = backActivity.getString("xmlTransaction");
    		ReturnOfTransactionXml mReturnOfTransactionXml;
    		mReturnOfTransactionXml = TransactionResponse.getTransaction(this, xmlTransaction, backActivity);
    		
    		Log.i("sdk_stone",
    						"\n\n======== Dados recebidos SDK ========"
    						+ "\nValor               : " + mReturnOfTransactionXml.amount
    						+ "\nARN                 : " + mReturnOfTransactionXml.arn
    						+ "\nParcelas            : " + mReturnOfTransactionXml.parcel
    						+ "\nBandeira            : " + mReturnOfTransactionXml.flag
    						+ "\nCA                  : " + mReturnOfTransactionXml.ca
    						+ "\nStatus              : " + mReturnOfTransactionXml.status
    						+ "\nData                : " + mReturnOfTransactionXml.date
    						+ "\nAmountOfInst.       : " + mReturnOfTransactionXml.amountOfInstallments
    						+ "\nDemandId            : " + mReturnOfTransactionXml.demandId
    						+ "\nTipo da transação   : " + mReturnOfTransactionXml.transactionType);
    		
    		dialogAnswer(mReturnOfTransactionXml);
    	}
	}
    
    // Cria um Dialog que informa ao usuário do app de integração a situação da transação.
    private void dialogAnswer(ReturnOfTransactionXml mReturnOfTransactionXml) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
			}
		});
    	
    	if (mReturnOfTransactionXml.status.equals("APR")) {
    		builder.setTitle("Transação Aprovada");
    		builder.setMessage("Transação de R$" + mReturnOfTransactionXml.amount + " foi aprovada com sucesso!");
    	} else if (mReturnOfTransactionXml.status.equals("DEC")) {
    		builder.setTitle("Transação Negada");
    		builder.setMessage("Transação negada. Tente novamente.");
    	} else {
    		builder.setTitle("Transação Abortada");
    		builder.setMessage("Transação cancelada pelo usuário.");
    	}
    	
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }
    
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
	}
}
