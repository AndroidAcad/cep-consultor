package br.com.androidacad.consultordeceps.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.androidacad.consultordeceps.models.CepResponse;
import br.com.androidacad.consultordeceps.R;

public class RuaAdapter extends ArrayAdapter<CepResponse> {
    private final Context context;
    private final List<CepResponse> ruas;

    public RuaAdapter(Context context, List<CepResponse> ruas) {
        super(context, R.layout.item_rua, ruas);
        this.context = context;
        this.ruas = ruas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_rua, parent, false);
        }

        CepResponse rua = ruas.get(position);
        TextView txtItemRua = itemView.findViewById(R.id.txt_item_rua);
        Button btnCopiarCep = itemView.findViewById(R.id.btn_copiar_cep);


        String enderecoCompleto = rua.getLogradouro() + ", " + rua.getBairro() + " - " + rua.getLocalidade() + "/" + rua.getUf();

        // Configure o TextView para exibir o logradouro (ou outro valor relevante)
        txtItemRua.setText(enderecoCompleto);

        // define CEP no botão
        btnCopiarCep.setText("Copy CEP: " + rua.getCep().toString());
        // Configurar o clique do botão para copiar o CEP
        btnCopiarCep.setOnClickListener(v -> {
            // Copiar o CEP para a área de transferência
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("CEP", rua.getCep());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "CEP copiado: " + rua.getCep(), Toast.LENGTH_SHORT).show();
        });

        return itemView;
    }
}
