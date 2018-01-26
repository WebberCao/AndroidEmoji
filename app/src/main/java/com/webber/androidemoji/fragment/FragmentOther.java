package com.webber.androidemoji.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webber.androidemoji.R;

public class FragmentOther extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_empty,null);
        TextView tv= (TextView) rootView.findViewById(R.id.tv);

        tv.setText(args.getString("Integer"));
        return rootView ;
    }
}
