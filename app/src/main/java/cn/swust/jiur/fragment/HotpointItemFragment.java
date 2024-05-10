package cn.swust.jiur.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import cn.swust.jiur.R;
import cn.swust.jiur.adapter.HotPointAdapter;
import cn.swust.jiur.databinding.FragmentHotpointItemBinding;
import cn.swust.jiur.entity.HotPoint;
import cn.swust.jiur.factory.DialogFactory;
import cn.swust.jiur.utils.FetchDataUtils;
import cn.swust.jiur.viewmodel.JsonDataViewModle;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HotpointItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotpointItemFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private Context context;
    private FragmentHotpointItemBinding binding;
    private HotPointAdapter adapter;
    private JsonDataViewModle viewModle;
    private Dialog dialog;
    public HotpointItemFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HotpointItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotpointItemFragment newInstance(Context context, String param1) {
        HotpointItemFragment fragment = new HotpointItemFragment(context);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHotpointItemBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = DialogFactory.loadDialog(getContext());
        dialog.setCancelable(false);
        dialog.show();
        viewModle = new ViewModelProvider(this).get(JsonDataViewModle.class);
        viewModle.getCatagoryDatas().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                if(jsonObject != null){
                    HotPoint hotPoint = JSON.parseObject(jsonObject.toString(), HotPoint.class);
                    adapter = new HotPointAdapter(hotPoint.getData(),getContext());
                    binding.recycleviewHotpoint.setAdapter(adapter);
                }
                dialog.dismiss();
            }
        });

        FetchDataUtils.fetchData("https://api.pearktrue.cn/api/social/hotlist.php?type="+mParam1,
                viewModle.getCatagoryDatas());
    }
}