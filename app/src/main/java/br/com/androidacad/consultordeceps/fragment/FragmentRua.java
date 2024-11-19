package br.com.androidacad.consultordeceps.fragment;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.androidacad.consultordeceps.R;
import br.com.androidacad.consultordeceps.adapter.RuaAdapter;
import br.com.androidacad.consultordeceps.models.CepResponse;
import br.com.androidacad.consultordeceps.services.ViaCepService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentRua extends Fragment {

    private EditText edtUf, edtCidade, edtRua;
    private ListView lvResultadoRua;
    private RuaAdapter ruaAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rua, container, false);

        edtUf = view.findViewById(R.id.edt_uf_input);
        edtCidade = view.findViewById(R.id.edt_cidade_input);
        edtRua = view.findViewById(R.id.edt_rua_input);
        lvResultadoRua = view.findViewById(R.id.lv_resultado_rua);

        Button btnBuscarRua = view.findViewById(R.id.btn_buscar_rua);
        Button btnLimparRua = view.findViewById(R.id.btn_limpar_rua);

        btnBuscarRua.setOnClickListener(v -> buscarRua());
        btnLimparRua.setOnClickListener(v -> limparCampos());

        // Impede o menu de ações de copiar/colar do teclado
        edtUf.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false; // Não cria o modo de ação
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Não prepara o modo de ação
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false; // Não lida com cliques nos itens de ação
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Não faz nada
            }
        });

        return view;
    }

    private void buscarRua() {
        String uf = edtUf.getText().toString().trim();
        String cidade = edtCidade.getText().toString().trim();
        String logradouro = edtRua.getText().toString().trim();

        if (uf.isEmpty() || cidade.isEmpty() || logradouro.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ViaCepService viaCepService = retrofit.create(ViaCepService.class);

        Call<List<CepResponse>> call = viaCepService.getRua(uf, cidade, logradouro);
        call.enqueue(new Callback<List<CepResponse>>() {
            @Override
            public void onResponse(Call<List<CepResponse>> call, Response<List<CepResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CepResponse> ruas = response.body();
                    ruaAdapter = new RuaAdapter(getContext(), ruas);
                    lvResultadoRua.setAdapter(ruaAdapter);
                } else {
                    Toast.makeText(getContext(), "Nenhum resultado encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CepResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Erro na busca. Verifique se há conexão com a internet!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limparCampos() {
        edtUf.setText("");
        edtCidade.setText("");
        edtRua.setText("");
        lvResultadoRua.setAdapter(null);
    }
}
