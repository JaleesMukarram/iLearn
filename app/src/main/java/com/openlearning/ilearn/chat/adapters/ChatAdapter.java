package com.openlearning.ilearn.chat.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.DocumentChat;
import com.openlearning.ilearn.chat.queries.FirebaseGlobals;
import com.openlearning.ilearn.chat.queries.ImageChat;
import com.openlearning.ilearn.chat.queries.MessageChat;
import com.openlearning.ilearn.databinding.ViewDocumentChatReceiveBinding;
import com.openlearning.ilearn.databinding.ViewDocumentChatSendBinding;
import com.openlearning.ilearn.databinding.ViewImageChatReceiveBinding;
import com.openlearning.ilearn.databinding.ViewImageChatSendBinding;
import com.openlearning.ilearn.databinding.ViewMessageChatReceiveBinding;
import com.openlearning.ilearn.databinding.ViewMessageChatSendBinding;
import com.openlearning.ilearn.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.openlearning.ilearn.utils.CommonUtils.getRelativeDate;
import static com.openlearning.ilearn.utils.CommonUtils.getSize;
import static com.openlearning.ilearn.utils.CommonUtils.openThisDocumentViaUri;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int MESSAGE_CHAT_SEND = 2;
    public static final int MESSAGE_CHAT_RECEIVED = 4;
    public static final int IMAGE_CHAT_SENT = 8;
    public static final int IMAGE_CHAT_RECEIVED = 16;
    public static final int DOCUMENT_CHAT_SENT = 32;
    public static final int DOCUMENT_CHAT_RECEIVED = 64;
    private static final String TAG = "ChatAdapterTAG";

    private final Context context;

    private List<Chat> chatList;
    private final String SENDING_USER;
    private final String RECEIVING_USER;


    public ChatAdapter(Context context, List<Chat> chatList, String SENDING_USER, String RECEIVING_USER) {
        this.context = context;
        this.chatList = chatList;
        this.SENDING_USER = SENDING_USER;
        this.RECEIVING_USER = RECEIVING_USER;

    }

    @Override
    public int getItemViewType(int position) {

        if (chatList.get(position) instanceof MessageChat) {

            if (chatList.get(position).getSendingUserID().equals(SENDING_USER)) {

                return MESSAGE_CHAT_SEND;

            } else if (chatList.get(position).getSendingUserID().equals(RECEIVING_USER)) {

                return MESSAGE_CHAT_RECEIVED;
            }

        } else if (chatList.get(position) instanceof ImageChat) {

            if (chatList.get(position).getSendingUserID().equals(SENDING_USER)) {

                return IMAGE_CHAT_SENT;

            } else if (chatList.get(position).getSendingUserID().equals(RECEIVING_USER)) {

                return IMAGE_CHAT_RECEIVED;
            }
        } else if (chatList.get(position) instanceof DocumentChat) {

            if (chatList.get(position).getSendingUserID().equals(SENDING_USER)) {

                return DOCUMENT_CHAT_SENT;

            } else if (chatList.get(position).getSendingUserID().equals(RECEIVING_USER)) {

                return DOCUMENT_CHAT_RECEIVED;
            }

        }

        return 2;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == MESSAGE_CHAT_SEND) {

            ViewMessageChatSendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_message_chat_send, parent, false);
            return new MessageChatSendView(binding);

        } else if (viewType == MESSAGE_CHAT_RECEIVED) {

            ViewMessageChatReceiveBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_message_chat_receive, parent, false);
            return new MessageChatReceiveView(binding);

        } else if (viewType == IMAGE_CHAT_SENT) {

            ViewImageChatSendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_image_chat_send, parent, false);
            return new ImageChatSendView(binding);

        } else if (viewType == IMAGE_CHAT_RECEIVED) {

            ViewImageChatReceiveBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_image_chat_receive, parent, false);
            return new ImageChatReceiveView(binding);

        } else if (viewType == DOCUMENT_CHAT_SENT) {

            ViewDocumentChatSendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_document_chat_send, parent, false);
            return new DocumentChatSendView(binding);

        } else if (viewType == DOCUMENT_CHAT_RECEIVED) {

            ViewDocumentChatReceiveBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_document_chat_receive, parent, false);
            return new DocumentChatReceiveView(binding);

        }

        ViewMessageChatSendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_message_chat_send, parent, false);
        return new MessageChatSendView(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MessageChatSendView) {

            MessageChat chat = (MessageChat) chatList.get(position);
            ((MessageChatSendView) holder).setData(chat);

        } else if (holder instanceof MessageChatReceiveView) {

            MessageChat chat = (MessageChat) chatList.get(position);
            ((MessageChatReceiveView) holder).setData(chat);

        } else if (holder instanceof ImageChatSendView) {

            ImageChat chat = (ImageChat) chatList.get(position);
            ((ImageChatSendView) holder).setData(chat);

        } else if (holder instanceof ImageChatReceiveView) {

            ImageChat chat = (ImageChat) chatList.get(position);
            ((ImageChatReceiveView) holder).setData(chat);

        } else if (holder instanceof DocumentChatSendView) {

            DocumentChat chat = (DocumentChat) chatList.get(position);
            ((DocumentChatSendView) holder).setData(chat);

        } else if (holder instanceof DocumentChatReceiveView) {

            DocumentChat chat = (DocumentChat) chatList.get(position);
            ((DocumentChatReceiveView) holder).setData(chat);

        }


    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: new size: " + chatList.size());
        return chatList.size();
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
    }

    //Views
    private class MessageChatSendView extends RecyclerView.ViewHolder {

        private final ViewMessageChatSendBinding mBinding;

        private MessageChatSendView(@NonNull ViewMessageChatSendBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

        }

        private void setData(MessageChat chat) {

            mBinding.TVChatMessage.setText(chat.getMessage());

            setPopUpListener(chat.getChatID());

            // If the chat is still sending or has been read
            if (chat.isChatSent()) {

                if (chat.getReadDate() != null) {

                    chatRead();
                    mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));


                } else if (chat.getSentDate() != null) {

//                    Log.d(TAG, "CHAT " + chat.getMessage() + " CHAT SENT " + chat.getSentDate());

                    chatSent();
                    String date = getRelativeDate(chat.getSentDate());
                    mBinding.TVMessageSentDate.setText(date);

                } else {

//                    Log.d(TAG, "STILL SENDING");
                    chatStillSending();
                }

            }

            // If the chat has been sending failed
            else {
                chatSentFailed(chat);
            }
        }

        private void chatRead() {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.VISIBLE);


            mBinding.IVMessageSentStatus.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_tick_received));

        }

        private void chatSent() {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.VISIBLE);

            mBinding.IVMessageSentStatus.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_tick_sent));

        }

        private void chatStillSending() {

            mBinding.RLProgressContainer.setVisibility(View.VISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendFailedOptionsContainer.setVisibility(View.INVISIBLE);

        }

        private void chatSentFailed(final MessageChat chat) {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendFailedOptionsContainer.setVisibility(View.VISIBLE);

            mBinding.RLSendFailedOptionsContainer.setOnClickListener(view -> {

//                    uploadThisMessageChatToDatabase(chat);
                chat.setChatSent(true);
//                    adapter.notifyDataSetChanged();
            });

        }

        private void setPopUpListener(final String chatID) {

//            MessageContainer.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//
//                    PopupMenu popupMenu = new PopupMenu(SendChat.this, view);
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//
//                            if (item.getItemId() == R.id.MIdelete) {
//                                deleteThisChatFromDatabase(chatID);
//                            }
//                            return false;
//                        }
//                    });
//
//                    popupMenu.inflate(R.menu.message_chat_option_menu);
//                    popupMenu.show();
//
//                    return true;
//                }
//            });
        }
    }

    private class MessageChatReceiveView extends RecyclerView.ViewHolder {

        private final ViewMessageChatReceiveBinding mBinding;

        private MessageChatReceiveView(@NonNull ViewMessageChatReceiveBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

        }

        @SuppressLint("SetTextI18n")
        private void setData(MessageChat chat) {

            mBinding.TVChatMessage.setText(chat.getMessage());
            mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));

        }

    }

    private class ImageChatSendView extends RecyclerView.ViewHolder {

        private final ViewImageChatSendBinding mBinding;

        private ImageChatSendView(@NonNull ViewImageChatSendBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }

        private void setData(ImageChat chat) {

            Picasso.get().load(chat.getImageUri())
                    .resize(400, 400)
                    .into(mBinding.IVImageChat);

            setMessageContainerListener(chat.getImageUri());

            if (chat.isChatSent()) {

                if (chat.getReadDate() != null) {

                    chatRead();
                    mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));

                } else if (chat.getSentDate() != null) {

                    chatSent();
                    mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));

                } else {

                    chatStillSending();

                }
            } else {

                chatSentFailed(chat);

            }
        }

        private void setMessageContainerListener(final String uri) {

            mBinding.RLTopContainer.setOnClickListener(view -> CommonUtils.fullScreenImageShowingDialogue(context, uri));

        }

        private void chatRead() {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.VISIBLE);


            mBinding.IVMessageSentStatus.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_tick_received));

        }

        private void chatSent() {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.VISIBLE);

            mBinding.IVMessageSentStatus.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_tick_sent));

        }

        private void chatSentFailed(final ImageChat chat) {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendFailedOptionsContainer.setVisibility(View.VISIBLE);

            mBinding.RLSendFailedOptionsContainer.setOnClickListener(view -> {

//                handleNewImageChat(chat);
//                chat.setChatSent(true);
//                adapter.notifyDataSetChanged();
            });

        }

        private void chatStillSending() {

            mBinding.RLProgressContainer.setVisibility(View.VISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendFailedOptionsContainer.setVisibility(View.INVISIBLE);

        }

    }

    private class ImageChatReceiveView extends RecyclerView.ViewHolder {

        private final ViewImageChatReceiveBinding mBinding;

        private ImageChatReceiveView(@NonNull ViewImageChatReceiveBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }

        private void setData(ImageChat chat) {

            Picasso.get().load(chat.getImageUri())
                    .resize(400, 400)
                    .into(mBinding.IVImageChat);

            setMessageContainerListener(chat.getImageUri());


            mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));

        }

        private void setMessageContainerListener(final String uri) {

            mBinding.RLTopContainer.setOnClickListener(view -> CommonUtils.fullScreenImageShowingDialogue(context, uri));

        }
    }

    private class DocumentChatSendView extends RecyclerView.ViewHolder {


        private final ViewDocumentChatSendBinding mBinding;

        public DocumentChatSendView(@NonNull ViewDocumentChatSendBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            // Download things


        }

        private void setData(final DocumentChat chat) {

            mBinding.TVSelectDocument.setText(chat.getDocumentName());

            mBinding.TVSelectDocumentSize.setText(getSize(chat.getDocumentSize()));

            if (chat.getDocumentName().endsWith(".pdf")) {

                mBinding.IVIconShowing.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_picture_as_pdf_24));
            } else {
                mBinding.IVIconShowing.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_file_24));
            }

            if (chat.isChatSent()) {


                if (chat.getReadDate() != null) {

                    chatRead();
                    checkDocumentDownloadedStatus(chat.getChatID() + chat.getDocumentName(), chat.getDocumentSize());
                    mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));


                } else if (chat.getSentDate() != null) {

                    chatSent();
                    checkDocumentDownloadedStatus(chat.getChatID() + chat.getDocumentName(), chat.getDocumentSize());
                    mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));

                } else {

                    chatStillSending(chat.getProgress());
                    mBinding.RLDocumentDownloadContainer.setVisibility(View.GONE);

                }
            } else {

                chatSentFailed(chat);
            }
        }

        //Chat sent status handling
        private void chatRead() {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.VISIBLE);


            mBinding.IVMessageSentStatus.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_tick_received));

        }

        private void chatSent() {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.VISIBLE);

            mBinding.IVMessageSentStatus.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_tick_sent));

        }

        private void chatSentFailed(final DocumentChat chat) {

            mBinding.RLProgressContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendFailedOptionsContainer.setVisibility(View.VISIBLE);

            mBinding.RLSendFailedOptionsContainer.setOnClickListener(view -> {

//                uploadDocumentChatToDatabase(chat);
                chat.setChatSent(true);
//                adapter.notifyDataSetChanged();
            });
        }


        private void chatStillSending(int progress) {

            mBinding.RLProgressContainer.setVisibility(View.VISIBLE);
            mBinding.RLSendOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLSendFailedOptionsContainer.setVisibility(View.INVISIBLE);
            mBinding.RLDocumentDownloadContainer.setVisibility(View.GONE);

            mBinding.PBSendingProgress.setProgress(progress);

        }

        // Document Download Handling
        private void checkDocumentDownloadedStatus(String documentName, long documentSize) {

//            if (validateThisDocumentInStorage(documentName, documentSize)) {
//
//                hideDownloadBox(documentName);
//
//            } else {
//
//                showDownloadBox(documentName);
//            }
//

        }

        private void hideDownloadBox(final String documentName) {

            Log.d(TAG, "Download box hidden");

            mBinding.RLDocumentDownloadContainer.setVisibility(View.GONE);

            mBinding.RLTopContainer.setOnClickListener(view -> openThisDocumentViaUri((Activity) context, documentName));

        }

        private void showDownloadBox(final String documentName) {

            mBinding.RLTopContainer.setOnClickListener(null);
            mBinding.RLDocumentDownloadContainer.setVisibility(View.VISIBLE);
            mBinding.PBDocumentDownloadProgress.setVisibility(View.INVISIBLE);

            mBinding.IVDocumentDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mBinding.IVDocumentDownload.setOnClickListener(null);
                    mBinding.PBDocumentDownloadProgress.setVisibility(View.VISIBLE);

                    downloadThisDocumentAndStoreToLocalStorage(documentName);
                }

            });

        }

        private void downloadThisDocumentAndStoreToLocalStorage(final String documentName) {

            //If directory is created or is available
            if (FirebaseGlobals.makeDocumentDirectory()) {

                String documentDirectory = "/" + documentName;

                File file = new File(FirebaseGlobals.Directory.LOCAL_DOCUMENT_DIRECTORY + documentDirectory);

                try {

                    if (file.createNewFile()) {

                        StorageReference reference = FirebaseStorage.getInstance().getReference(FirebaseGlobals.Storage.DOCUMENT_STORAGE_REFERENCE + documentDirectory);

                        reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                hideDownloadBox(documentName);
                                CommonUtils.openThisDocumentViaUri((Activity) context, documentName);
                                Toast.makeText(context, "File Downloaded..", Toast.LENGTH_SHORT).show();

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(context, "Error while downloading file", Toast.LENGTH_SHORT).show();
                                        showDownloadBox(documentName);
                                        Log.d(TAG, "Failed downloading file: " + e);

                                    }
                                })

                                .addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {

                                        showDownloadBox(documentName);
                                        Toast.makeText(context, "Error while downloading file", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Canceled");

                                    }
                                });


                    }
                } catch (IOException e) {

                    Log.d(TAG, "File not made: " + e.toString());

                }

            }
            //displaying error here
            else {

                Log.d(TAG, "Directory not made");
                Toast.makeText(context, "Error while making documents directory. check storage permissions", Toast.LENGTH_SHORT).show();
                showDownloadBox(documentName);
            }
        }

    }

    private class DocumentChatReceiveView extends RecyclerView.ViewHolder {

        private final ViewDocumentChatReceiveBinding mBinding;


        public DocumentChatReceiveView(@NonNull ViewDocumentChatReceiveBinding binding) {
            super(binding.getRoot());

            mBinding = binding;


            // Download things


        }

        private void setData(final DocumentChat chat) {

            mBinding.TVSelectDocument.setText(chat.getDocumentName());
            mBinding.TVSelectDocumentSize.setText(getSize(chat.getDocumentSize()));
            mBinding.TVMessageSentDate.setText(getRelativeDate(chat.getSentDate()));

            if (chat.getDocumentName().endsWith(".pdf")) {

                mBinding.IVIconShowing.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_picture_as_pdf_24));
            } else {
                mBinding.IVIconShowing.setImageDrawable(CommonUtils.getDrawable(context, R.drawable.ic_file_24));
            }

            checkDocumentDownloadedStatus(chat.getChatID() + chat.getDocumentName(), chat.getDocumentSize());


        }

        // Document Download Handling
        private void checkDocumentDownloadedStatus(String documentName, long documentSize) {

//            if (validateThisDocumentInStorage(documentName, documentSize)) {
//
//                hideDownloadBox(documentName);
//
//            } else {
//
//                showDownloadBox(documentName);
//            }

        }

        private void hideDownloadBox(final String documentName) {

            Log.d(TAG, "Download box hidden");

            mBinding.RLDocumentDownloadContainer.setVisibility(View.GONE);

            mBinding.RLTopContainer.setOnClickListener(view -> openThisDocumentViaUri((Activity) context, documentName));

        }

        private void showDownloadBox(final String documentName) {

            Log.d(TAG, "Box Shown for downloading");

            mBinding.RLTopContainer.setOnClickListener(null);
            mBinding.RLDocumentDownloadContainer.setVisibility(View.VISIBLE);
            mBinding.PBDocumentDownloadProgress.setVisibility(View.INVISIBLE);

            mBinding.IVDocumentDownload.setOnClickListener(view -> {

                mBinding.IVDocumentDownload.setOnClickListener(null);
                mBinding.PBDocumentDownloadProgress.setVisibility(View.VISIBLE);

                downloadThisDocumentAndStoreToLocalStorage(documentName);
            });

        }

        private void downloadThisDocumentAndStoreToLocalStorage(final String documentName) {

            //If directory is created or is available
            if (FirebaseGlobals.makeDocumentDirectory()) {

                String documentDirectory = "/" + documentName;

                File file = new File(FirebaseGlobals.Directory.LOCAL_DOCUMENT_DIRECTORY + documentDirectory);

                try {

                    if (file.createNewFile()) {

                        StorageReference reference = FirebaseStorage.getInstance().getReference(FirebaseGlobals.Storage.DOCUMENT_STORAGE_REFERENCE + documentDirectory);

                        reference.getFile(file).addOnSuccessListener(taskSnapshot -> {

                            hideDownloadBox(documentName);
                            openThisDocumentViaUri((Activity) context, documentName);
                            Toast.makeText(context, "File Downloaded..", Toast.LENGTH_SHORT).show();

                        })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(context, "Error while downloading file", Toast.LENGTH_SHORT).show();
                                    showDownloadBox(documentName);
                                    Log.d(TAG, "Failed downloading file: " + e);

                                })

                                .addOnCanceledListener(() -> {

                                    showDownloadBox(documentName);
                                    Toast.makeText(context, "Error while downloading file", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Canceled");

                                });


                    }
                } catch (IOException e) {

                    Log.d(TAG, "File not made: " + e.toString());

                }

            }
            //displaying error here
            else {

                Log.d(TAG, "Directory not made");
                Toast.makeText(context, "Error while making documents directory. check storage permissions", Toast.LENGTH_SHORT).show();
                showDownloadBox(documentName);
            }
        }

    }
}
