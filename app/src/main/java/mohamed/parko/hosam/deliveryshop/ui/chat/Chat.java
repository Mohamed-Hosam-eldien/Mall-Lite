package mohamed.parko.hosam.deliveryshop.ui.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.EventBus.RefreshAdapterEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.SendNotificationEvent;
import mohamed.parko.hosam.deliveryshop.Holder.LeftViewHolder;
import mohamed.parko.hosam.deliveryshop.Holder.RightViewHolder;
import mohamed.parko.hosam.deliveryshop.Model.ChatModel;
import mohamed.parko.hosam.deliveryshop.Model.FCMSendData;
import mohamed.parko.hosam.deliveryshop.Model.TokenModel;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityChatBinding;
import mohamed.parko.hosam.deliveryshop.remote.IFCMService;
import mohamed.parko.hosam.deliveryshop.remote.RetrofitFCMClient;


public class Chat extends AppCompatActivity {

    private ActivityChatBinding binding;
    private LinearLayoutManager layoutManager;
    private static final int MSG_TYPE_LIFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private ValueEventListener seenListener;
    private ChildEventListener typingListener;
    private DatabaseReference userRefForSeen, userRefForTyping;

    private FirebaseRecyclerAdapter<ChatModel, RecyclerView.ViewHolder> adapter;


    private CompositeDisposable compositeDisposable;
    private IFCMService ifcmService;
    private UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Paper.init(this);
        userModel = Paper.book().read("currentUser");
        if(Common.currentUser == null)
            Common.currentUser =userModel;

        init();

        seenMessage();
        setOnlineState("on");
        listenerForTypingState();

        loadMessages();

        binding.send.setOnClickListener(view -> {

            if (!binding.edtMessage.getText().toString().isEmpty()) {

                String timeStamp = String.valueOf(System.currentTimeMillis());
                String text = binding.edtMessage.getText().toString();

                ChatModel model = new ChatModel();
                model.setLastMessage(text);
                model.setName(Common.currentUser.getName());
                model.setPicture(false);
                model.setSeen(false);
                model.setTimeStamp(timeStamp);
                model.setUid(Common.currentUser.getUid());
                model.setPhone(Common.currentUser.getPhone());

                new Handler().postDelayed(() ->

                        FirebaseDatabase.getInstance()
                                .getReference(Common.chat_ref)
                                .child(Common.currentUser.getUid())
                                .child(Common.message_details)
                                .push().setValue(model).addOnCompleteListener(task ->
                                FirebaseDatabase.getInstance()
                                        .getReference(Common.chat_ref)
                                        .child(Common.currentUser.getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                snapshot.child("lastMessage").getRef().setValue(text);
                                                snapshot.child("timeStamp").getRef().setValue(timeStamp);
                                                snapshot.child("userTyping").getRef().setValue("no");
                                                snapshot.child("name").getRef().setValue(Common.currentUser.getName());
                                                snapshot.child("phone").getRef().setValue(Common.currentUser.getPhone());
                                                snapshot.child("uid").getRef().setValue(Common.currentUser.getUid());

                                                EventBus.getDefault().postSticky(new SendNotificationEvent(true, text));
                                                EventBus.getDefault().postSticky(new RefreshAdapterEvent(true));

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }

                                        })), 500);

                binding.edtMessage.setText("");

                //loadMessagesFromServer();
            }

        });

        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FirebaseDatabase.getInstance()
                        .getReference(Common.chat_ref)
                        .child(Common.currentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (charSequence.toString().trim().length() == 0) {
                                        setTypingState("no");
                                    } else {
                                        setTypingState("forServer");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    private void listenerForTypingState() {

        userRefForTyping = FirebaseDatabase.getInstance()
                .getReference(Common.chat_ref)
                .child(Common.currentUser.getUid());

        typingListener = userRefForTyping.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getKey().equalsIgnoreCase("serverTyping") && snapshot.getValue().toString().equals("no")) {
                            binding.textDotLoader.setVisibility(View.GONE);
                        } else if (snapshot.getKey().equalsIgnoreCase("serverTyping") && snapshot.getValue().toString().equals("forClient")) {
                            binding.textDotLoader.setVisibility(View.VISIBLE);
                            binding.textDotLoader.initAnimation();
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getKey().equalsIgnoreCase("serverTyping") && snapshot.getValue().toString().equals("no")) {
                            binding.textDotLoader.setVisibility(View.GONE);
                        } else if (snapshot.getKey().equalsIgnoreCase("serverTyping") && snapshot.getValue().toString().equals("forClient")) {
                            binding.textDotLoader.setVisibility(View.VISIBLE);
                            binding.textDotLoader.initAnimation();
                        }
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});

    }

    private void sendNotification(String message) {

        Map<String, String> notiData = new HashMap<>();
        notiData.put(Common.NOTI_TITLE, "رسالة جديدة");
        notiData.put(Common.NOTI_CONTENT, Common.currentUser.getName() + " : " + message);

        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot post : snapshot.getChildren()) {

                            TokenModel model = post.getValue(TokenModel.class);
                            if (model.isAdmin() && model.getOnlineState().equals("off")) {

                                FCMSendData sendData = new FCMSendData(model.getToken(), notiData);

                                compositeDisposable.add(ifcmService.sendNotification(sendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {

                                        }, throwable -> {

                                        }));

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void init() {

        binding.recycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recycler.setLayoutManager(layoutManager);
        compositeDisposable = new CompositeDisposable();
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        binding.imgBack.setOnClickListener(view -> finish());

    }

    private void setOnlineState(String state) {
        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REF)
                .child(Common.currentUser.getUid())
                .child("onlineState").setValue(state);
    }

    private void loadMessages() {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.chat_ref)
                .child(Common.currentUser.getUid())
                .child(Common.message_details);

        FirebaseRecyclerOptions<ChatModel> options = new FirebaseRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ChatModel, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatModel model) {

                holder.setIsRecyclable(false);

                if (holder instanceof LeftViewHolder) {

                    LeftViewHolder holderLeft = (LeftViewHolder) holder;

                    holderLeft.setIsRecyclable(false);
                    holderLeft.txtMessage.setText(adapter.getItem(position).getLastMessage());
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(adapter.getItem(position).getTimeStamp()));
                    holderLeft.txtTime.setText(DateFormat.format("hh:mm aa", calendar).toString());

                    //if (position == adapter.getItemCount() - 1)
                    holderLeft.imgMessageState.setVisibility(View.GONE);

                    /*if (adapter.getItem(position).isSeen()) {
                        holderLeft.imgMessageState.setImageResource(R.drawable.ic_baseline_done_all_24);
                    } else
                        holderLeft.imgMessageState.setImageResource(R.drawable.ic_baseline_done_24);*/

                } else {

                    RightViewHolder holderRight = (RightViewHolder) holder;

                    holderRight.setIsRecyclable(false);
                    holderRight.txtMessage.setText(adapter.getItem(position).getLastMessage());
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(Long.parseLong(adapter.getItem(position).getTimeStamp()));
                    holderRight.txtTime.setText(DateFormat.format("hh:mm aa", calendar).toString());

                    if (position != adapter.getItemCount() - 1)
                        holderRight.imgMessageState.setVisibility(View.GONE);

                    if (adapter.getItem(position).isSeen()) {
                        holderRight.imgMessageState.setImageResource(R.drawable.ic_baseline_done_all_24);
                        //EventBus.getDefault().postSticky(new RefreshAdapterEvent(true));
                    } else {
                        holderRight.imgMessageState.setImageResource(R.drawable.ic_baseline_done_all_gray_24);
                    }

                }

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == MSG_TYPE_LIFT) {
                    return new LeftViewHolder(LayoutInflater.from(Chat.this).inflate(R.layout.chat_layout_left, parent, false));
                } else {
                    return new RightViewHolder(LayoutInflater.from(Chat.this).inflate(R.layout.chat_layout_right, parent, false));
                }
            }

            @Override
            public int getItemViewType(int position) {

                if (adapter.getItem(position).getUid().equals(Common.currentUser.getUid()))
                    return MSG_TYPE_RIGHT;
                else
                    return MSG_TYPE_LIFT;

            }

            @Override
            public void onDataChanged() {
                EventBus.getDefault().postSticky(new RefreshAdapterEvent(true));
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int frindlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (frindlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {

                    binding.recycler.scrollToPosition(positionStart);
                }

            }
        });

        adapter.startListening();
        adapter.notifyDataSetChanged();
        binding.recycler.setAdapter(adapter);

    }

    public void seenMessage() {

        userRefForSeen = FirebaseDatabase.getInstance()
                .getReference(Common.chat_ref)
                .child(Common.currentUser.getUid()).child(Common.message_details);

        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot post : snapshot.getChildren()) {

                    ChatModel model = post.getValue(ChatModel.class);
                    if (!model.getUid().equals(Common.currentUser.getUid())) {

                        HashMap<String, Object> updateData = new HashMap<>();
                        updateData.put("seen", true);
                        post.getRef().updateChildren(updateData);
                        //EventBus.getDefault().postSticky(new RefreshAdapterEvent(true,position));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void setTypingState(String state) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("userTyping", state);

        FirebaseDatabase.getInstance()
                .getReference(Common.chat_ref)
                .child(Common.currentUser.getUid()).getRef().updateChildren(data);

    }

    public void setTypingStateForOnPause(String state) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("userTyping", state);

        FirebaseDatabase.getInstance()
                .getReference(Common.chat_ref)
                .child(Common.currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            snapshot.getRef().updateChildren(data);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        binding = null;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshAdapterEvent(RefreshAdapterEvent event) {
        if (event.isRefresh()) {
            adapter.notifyDataSetChanged();
            adapter.startListening();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void sendNotificationEvent(SendNotificationEvent event) {
        if (event.isSend()) {
            sendNotification(event.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
        userRefForTyping.removeEventListener(typingListener);
        setOnlineState("off");
        setTypingStateForOnPause("no");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        EventBus.getDefault().removeAllStickyEvents();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.currentUser == null)
            Common.currentUser = userModel;

        if (adapter != null)
            adapter.startListening();
    }


}