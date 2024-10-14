package br.com.androidacad.consultordeceps.services;

import java.util.List;

import br.com.androidacad.consultordeceps.models.CepResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCepService {
    @GET("ws/{cep}/json/")
    Call<CepResponse> getCep(@Path("cep") String cep);

    @GET("ws/{uf}/{cidade}/{logradouro}/json/")
    Call<List<CepResponse>> getRua(
            @Path("uf") String uf,
            @Path("cidade") String cidade,
            @Path("logradouro") String logradouro
    );
}
