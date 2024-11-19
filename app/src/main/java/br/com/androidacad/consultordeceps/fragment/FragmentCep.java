package br.com.androidacad.consultordeceps.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.com.androidacad.consultordeceps.R;
import br.com.androidacad.consultordeceps.models.CepResponse;
import br.com.androidacad.consultordeceps.services.ViaCepService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentCep extends Fragment {

    private EditText edtCepInput;
    private TextView txtCepResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cep, container, false);

        // Inicializar os campos
        edtCepInput = view.findViewById(R.id.edt_cep_input);
        txtCepResult = view.findViewById(R.id.txt_cep_result);
        Button btnBuscarCep = view.findViewById(R.id.btn_buscar_cep);
        Button btnLimpar = view.findViewById(R.id.btn_limpar);

        // Configuração do Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ViaCepService cepApi = retrofit.create(ViaCepService.class);

        // Ação do botão para buscar o CEP
        btnBuscarCep.setOnClickListener(v -> {
            String cep = edtCepInput.getText().toString().trim();

            if (cep.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, insira um CEP.", Toast.LENGTH_SHORT).show();
            } else {
                // Fazer a requisição à API
                Call<CepResponse> call = cepApi.getCep(cep);
                call.enqueue(new Callback<CepResponse>() {
                    @Override
                    public void onResponse(Call<CepResponse> call, Response<CepResponse> response) {
                        if (!response.isSuccessful()) {
                            txtCepResult.setText("Utilize formatos 99999-999 ou 99999999 sem espaço e apenas numeros. Exemplos de formatos inválido que não devem ser usados: \"950100100\" (9 dígitos), \"95010A10\" (alfanumérico), \"95010 10\" (espaço).");
                            Toast.makeText(getActivity(), "Por favor, insira um CEP Válido!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Obter a resposta e atualizar a interface
                        CepResponse cepResponse = response.body();
                        if (cepResponse != null && cepResponse.getLogradouro() != null) {
                            String resultado = "Logradouro: " + cepResponse.getLogradouro() + "\n" +
                                    "Bairro: " + cepResponse.getBairro() + "\n" +
                                    "Cidade: " + cepResponse.getLocalidade() + "\n" +
                                    "UF: " + cepResponse.getUf() + "\n" +
                                    "CEP: " + cepResponse.getCep();
                            txtCepResult.setText(resultado);
                        } else {
                            txtCepResult.setText("CEP não encontrado.");
                        }
                    }

                    @Override
                    public void onFailure(Call<CepResponse> call, Throwable t) {
                        Log.e("FragmentCep", "Erro na busca do CEP: ", t);
                        txtCepResult.setText("Erro ao buscar o CEP.");
                        Toast.makeText(getActivity(), "Erro ao buscar o CEP. Verifique se há conexão com a internet!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Ação do botão "Limpar"
        btnLimpar.setOnClickListener(v -> {
            edtCepInput.setText(""); // Limpa o campo de entrada de CEP
            txtCepResult.setText(""); // Limpa o resultado exibido
        });

        // Habilita o clique longo para colar texto
        edtCepInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasteOption(v);
            }
        });
        return view;
    }

    private void showPasteOption(View anchor) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_item_paste) {
                pasteFromClipboard();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void pasteFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String text = item.getText().toString();
            if (text.length() <= 9) {
                edtCepInput.setText(text);
            } else {
                Toast.makeText(getActivity(), "Texto copiado deve ter no máximo 9 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
