package br.com.androidacad.consultordeceps.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter; // Alterado para FragmentStatePagerAdapter

import br.com.androidacad.consultordeceps.fragment.FragmentCep;
import br.com.androidacad.consultordeceps.fragment.FragmentRua;

public class ViewPagerAdapter extends FragmentStatePagerAdapter { // Alterado para FragmentStatePagerAdapter

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT); // Adicionado BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentCep(); // Fragment para consulta de CEP
            case 1:
                return new FragmentRua(); // Fragment para consulta de Endereço
            default:
                return new FragmentCep();
        }
    }

    @Override
    public int getCount() {
        return 2; // Número de Fragments
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Consultar CEP"; // Corrigido "Colsultar" para "Consultar"
            case 1:
                return "Consultar Rua"; // Corrigido "Colsultar" para "Consultar"
            default:
                return null;
        }
    }
}
