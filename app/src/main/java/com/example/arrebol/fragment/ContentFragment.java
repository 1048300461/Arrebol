package com.example.arrebol.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arrebol.R;

public class ContentFragment extends Fragment {
    private TextView content_tv;
    private int id;

    public ContentFragment(int id){
        this.id = id;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        content_tv = view.findViewById(R.id.novel_content_tv);
        content_tv.setText("第" + id + "页" + "2020-08-07 15:21:37.278 6389-6389/com.example.arrebol D/zcc: getItem: 0\n" +
                "2020-08-07 15:21:37.278 6389-6389/com.example.arrebol D/zcc: getItem: 1\n" +
                "2020-08-07 15:21:38.645 6389-6389/com.example.arrebol D/zcc: getItem: 2\n" + "首先准备好TextView   和测量字符如\n" +
                "<!--用来测量一行可以显示多少字符   边距统一用padding-->\n" +
                "<TextView\n" +
                "    android:id=\"@+id/article_ranging\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"wrap_content\"\n" +
                "    android:paddingLeft=\"24dp\"\n" +
                "    android:paddingRight=\"24dp\"\n" +
                "    android:visibility=\"gone\"\n" +
                "    android:text=\"abcdefghijklmnopqrstuvwxyz\"/>\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 用  TextView  测量不同分辨率  下  一行能显示多少个字符\n" +
                "     */\n" +
                "    @SuppressLint(\"NewApi\")\n" +
                "    public static int getLineMaxNumber(Activity activity, TextView textView) {\n" +
                "        //获取用来测量的字符串的长度\n" +
                "        int str_length = MyApplication.appcontext.getResources().getString(R.string.ranging_str).length();\n" +
                "        //获取当前字符串所占的宽度   像素单位\n" +
                "        int total_str_dpi = (int) textView.getPaint().measureText(textView.getText().toString());\n" +
                "        //获取每个字符   占多少像素\n" +
                "        int c_dpi = total_str_dpi / str_length;\n" +
                "        //获取当前   手机的分辨率   获取横坐标像素\n" +
                "        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();\n" +
                "        float total_dpi = displayMetrics.widthPixels;\n" +
                "        //获取边距像素\n" +
                "        int parding_dpi=textView.getTotalPaddingLeft()+textView.getTotalPaddingRight();\n" +
                "//        int parding_dpi=0;\n" +
                "        //总宽像素   减去   边距像素   等于  最终显示一行字符的像素宽度\n" +
                "        int end_total_dpi=(int) total_dpi-parding_dpi;\n" +
                "        //总像素宽度   处于   单个字符占的宽度像素   得到一行占多少字符\n" +
                "        return  end_total_dpi / c_dpi;\n" +
                "    }");
        return view;
    }
}
