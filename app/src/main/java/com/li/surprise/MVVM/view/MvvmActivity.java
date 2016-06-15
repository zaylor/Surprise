package com.li.surprise.MVVM.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.li.surprise.R;
import com.li.surprise.databinding.CollectionsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MvvmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CollectionsBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_mvvm);

        String[] literals = new String[]{"liang", "fei"};

        List<String> list = new ArrayList<>();
        SparseArray<String> sparse = new SparseArray<>(2);

        String key = "firstName";
        int index = 0;

        for (int i = 0; i < literals.length; i++) {
            list.add(literals[i]);
            sparse.put(0, literals[i]);
        }

        Map<String, String> map = new HashMap<>();
        map.put(key, "liang");
        map.put("lastName", "fei");

        binding.setIndex(index);
        binding.setKey(key);
        binding.setList(list);
        binding.setSparse(sparse);
        binding.setMap(map);
    }
}
