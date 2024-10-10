package br.com.androidacad.consultordeceps.services;

import br.com.androidacad.consultordeceps.models.CepResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCepService {
    @GET("ws/{cep}/json/")
    Call<CepResponse> getCep(@Path("cep") String cep);
}
