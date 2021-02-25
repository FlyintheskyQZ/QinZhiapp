package seu.qz.qzapp.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.util.List;

import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmMessageType;
import seu.qz.qzapp.R;
import seu.qz.qzapp.agora.MessageBean;
import seu.qz.qzapp.entity.AppCustomer;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    //数据源，封装的Message的List
    private List<MessageBean> messageBeanList;
    //来自AgoraChatActivity的布局配置器
    private LayoutInflater inflater;
    //子项监听器，此回调接口用于同Activity交互
    private OnItemClickListener listener;
    //当前登录用户
    private AppCustomer customer;

    public MessageAdapter(Context context, List<MessageBean> messageBeanList, AppCustomer customer, @NonNull OnItemClickListener listener) {
        this.inflater = ((Activity) context).getLayoutInflater();
        this.messageBeanList = messageBeanList;
        this.listener = listener;
        this.customer = customer;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.msg_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        setupView(holder, position);
    }

    @Override
    public int getItemCount() {
        return messageBeanList.size();
    }

    private void setupView(MyViewHolder holder, int position) {
        final MessageBean bean = messageBeanList.get(position);
        //如果是本地发送的消息，则消息和用户名称都放在右边
        if (bean.isBeSelf()) {
            holder.textViewSelfName.setText(bean.getAccount());
            //如果是对方发送的消息，则消息和用户名称都放在左边
        } else {
            holder.textViewOtherName.setText(bean.getAccount());
            //设置头像背景颜色，我猜是群聊时用的
            if (bean.getBackground() != 0) {
                holder.textViewOtherName.setBackgroundResource(bean.getBackground());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(bean);
                }
            }
        });
        //获取实际的RtmMessage
        RtmMessage rtmMessage = bean.getMessage();
        //根据消息类型进行配置
        switch (rtmMessage.getMessageType()) {
            //如果是文本消息
            case RtmMessageType.TEXT:
                //本地发出的消息，放置在右边
                if (bean.isBeSelf()) {
                    holder.textViewSelfMsg.setVisibility(View.VISIBLE);
                    String text = rtmMessage.getText();
                    if(text.startsWith(customer.getUser_nickName() + "-" + String.valueOf(customer.isMale()) + ":")){
                        holder.textViewSelfMsg.setText(text.substring(text.indexOf(":") + 1));
                    }else {
                        holder.textViewSelfMsg.setText(text);
                    }

                    //对方发出的消息，放在左边
                } else {
                    holder.textViewOtherMsg.setVisibility(View.VISIBLE);
                    holder.textViewOtherMsg.setText(rtmMessage.getText());
                }
                //非文本消息，则不可见对应控件
                holder.imageViewSelfImg.setVisibility(View.GONE);
                holder.imageViewOtherImg.setVisibility(View.GONE);
                break;
                //如果是图片消息
            case RtmMessageType.IMAGE:
                //使用Glide获取图片信息
                RtmImageMessage rtmImageMessage = (RtmImageMessage) rtmMessage;
                RequestBuilder<Drawable> builder = Glide.with(holder.itemView)
                        .load(rtmImageMessage.getThumbnail())
                        .override(rtmImageMessage.getThumbnailWidth(), rtmImageMessage.getThumbnailHeight());
                //如果是本地消息，放在右边，如果是对方消息，放在左边
                if (bean.isBeSelf()) {
                    holder.imageViewSelfImg.setVisibility(View.VISIBLE);
                    builder.into(holder.imageViewSelfImg);
                } else {
                    holder.imageViewOtherImg.setVisibility(View.VISIBLE);
                    builder.into(holder.imageViewOtherImg);
                }

                holder.textViewSelfMsg.setVisibility(View.GONE);
                holder.textViewOtherMsg.setVisibility(View.GONE);
                break;
        }
        //本地消息右边可见，对方消息左边可见
        holder.layoutRight.setVisibility(bean.isBeSelf() ? View.VISIBLE : View.GONE);
        holder.layoutLeft.setVisibility(bean.isBeSelf() ? View.GONE : View.VISIBLE);
    }

    public interface OnItemClickListener {
        void onItemClick(MessageBean message);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOtherName;
        private TextView textViewOtherMsg;
        private ImageView imageViewOtherImg;
        private TextView textViewSelfName;
        private TextView textViewSelfMsg;
        private ImageView imageViewSelfImg;
        private RelativeLayout layoutLeft;
        private RelativeLayout layoutRight;

        MyViewHolder(View itemView) {
            super(itemView);

            textViewOtherName = itemView.findViewById(R.id.item_name_l);
            textViewOtherMsg = itemView.findViewById(R.id.item_msg_l);
            imageViewOtherImg = itemView.findViewById(R.id.item_img_l);
            textViewSelfName = itemView.findViewById(R.id.item_name_r);
            textViewSelfMsg = itemView.findViewById(R.id.item_msg_r);
            imageViewSelfImg = itemView.findViewById(R.id.item_img_r);
            layoutLeft = itemView.findViewById(R.id.item_layout_l);
            layoutRight = itemView.findViewById(R.id.item_layout_r);
        }
    }

}