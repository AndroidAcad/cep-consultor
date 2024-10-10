package br.com.androidacad.consultordeceps.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Importações do Google Maps
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.androidacad.consultordeceps.R;
import br.com.androidacad.consultordeceps.models.CepResponse;
import br.com.androidacad.consultordeceps.services.ViaCepService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RootActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText cepInput;
    private TextView resultado;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        cepInput = findViewById(R.id.cep_input);
        resultado = findViewById(R.id.resultado);
        Button btnConsultar = findViewById(R.id.btn_consultar);
        Button btnLimpar = findViewById(R.id.btn_limpar);

        // Ação do botão "Consultar"
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarCep(cepInput.getText().toString());
            }
        });

        // Ação do botão "Limpar"
        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cepInput.setText(""); // Limpa o campo de CEP
                resultado.setText(""); // Limpa o resultado
                if (mMap != null) {
                    mMap.clear(); // Limpa o mapa
                }
            }
        });

        // Acionar a busca quando o usuário pressionar "Enter"
        cepInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    consultarCep(cepInput.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Inicializar o mapa (comentado aguardando a ativação da API do Google Maps)
        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void consultarCep(String cep) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ViaCepService service = retrofit.create(ViaCepService.class);
        Call<CepResponse> call = service.getCep(cep);
        call.enqueue(new Callback<CepResponse>() {
            @Override
            public void onResponse(Call<CepResponse> call, Response<CepResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CepResponse endereco = response.body();
                    resultado.setText("Rua: " + endereco.getLogradouro() + "\n" +
                            "Bairro: " + endereco.getBairro() + "\n" +
                            "Cidade: " + endereco.getLocalidade() + "\n" +
                            "Estado: " + endereco.getUf());

                    // Exemplo de coordenadas para a localização aproximada
                    LatLng location = new LatLng(-23.5505, -46.6333); // São Paulo
                    if (mMap != null) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(location).title("Localização aproximada"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                    }

                } else {
                    resultado.setText("CEP não encontrado.");
                }
            }

            @Override
            public void onFailure(Call<CepResponse> call, Throwable t) {
                resultado.setText("Erro: " + t.getMessage());
            }
        });
    }
}
