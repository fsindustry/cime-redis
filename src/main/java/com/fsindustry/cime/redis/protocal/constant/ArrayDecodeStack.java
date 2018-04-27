package com.fsindustry.cime.redis.protocal.constant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * 存放array解析过程中的数据
 * <p>
 * 解码array类型时，存放嵌套情况，为保证数据的高效解析，需要记录保存点及已经解析完成的数据；
 *
 * @author fuzhengxin
 * @date 2018/4/23
 */
public final class ArrayDecodeStack {

    /**
     * 存放当前消息所有嵌套的array信息；
     * 以栈的形式存放；
     */
    private final LinkedList<State> stack;

    public ArrayDecodeStack() {
        stack = new LinkedList<>();
    }

    public State pop() {
        return stack.pop();
    }

    public int deepth() {
        return stack.size();
    }

    public void push(State state) {
        stack.push(state);
    }

    /**
     * 存放一个array的状态信息
     */
    @Getter
    public static final class State {

        private int size;

        /**
         * 存放已经读取的数据
         */
        private final List<Object> datas;

        public State(int size) {
            this.size = size;
            this.datas = new ArrayList<>(size);
        }

        public void add(Object data) {
            datas.add(data);
        }

        public int idx() {
            return datas.size();
        }
    }
}
