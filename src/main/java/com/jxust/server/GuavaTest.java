package com.jxust.server;

import com.google.common.base.Joiner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuavaTest {


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("ssss");
        list.add("ttt");
        list.add("hahahah");
        String join = Joiner.on(",").join(list);
        System.out.println("join str: "+join);
        StringBuilder sb = new StringBuilder();
        sb.append("start:").append("list to string begin:");
        StringBuilder stringBuilder = Joiner.on(",").appendTo(sb, list);
        System.out.println(stringBuilder.toString());
        String str = Joiner.on(' ').useForNull("None").join(1, null, 3);
        System.out.println(str);
    }
}
