package com.moloxor.mynotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NOTE = 1;
    private static final int VIEW_TYPE_BTNDELETE = 2;
    private List<Note> notes = new ArrayList<>();
    private NoteViewHolder.NoteViewCallBack callBack;
    private Context context;

    public NotesAdapter(NoteViewHolder.NoteViewCallBack callBack, Context context) {

        this.callBack = callBack;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_NOTE:
                View view = layoutInflater.inflate(R.layout.item_note, parent, false);
                return new NoteViewHolder(view, callBack);

            case VIEW_TYPE_BTNDELETE:
                View view1 = layoutInflater.inflate(R.layout.item_delete_notes, parent, false);
                return new DeleteAllNotesViewHolder(view1);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoteViewHolder) {

            ((NoteViewHolder) holder).bindNote(notes.get(position));

        } else if (holder instanceof DeleteAllNotesViewHolder) {
            ((DeleteAllNotesViewHolder) holder).btnDeleteAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notifyItemRangeRemoved(0, notes.size());
                    notes.clear();
                    checkDeleteAllNotesState();
                    NotesDB notesDB = new NotesDB(context);
                    notesDB.deleteAll();

                }
            });

            ((DeleteAllNotesViewHolder) holder).setButtonVisibilaty(notes.size() > 1 ? View.VISIBLE : View.GONE);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == notes.size() ? VIEW_TYPE_BTNDELETE : VIEW_TYPE_NOTE;
    }

    @Override
    public int getItemCount() {
        return notes.size() + 1;
    }

    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
        checkDeleteAllNotesState();
    }

    public void addNote(Note note) {
        notes.add(note);
        notifyItemInserted(notes.size() - 1);
        checkDeleteAllNotesState();
    }

    public void updateNote(int position, String title, String content) {
        notes.get(position).setTitle(title);
        notes.get(position).setContent(content);
        notifyItemChanged(position);
    }

    private void checkDeleteAllNotesState() {
        notifyItemChanged(notes.size());
    }


    private static class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvContent;
        private ImageView ivEdit;
        private ImageView ivDelete;
        private NoteViewCallBack callBack;

        public NoteViewHolder(View itemView, final NoteViewCallBack callBack) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvContent = itemView.findViewById(R.id.tv_note_content);
            ivEdit = itemView.findViewById(R.id.iv_note_edit);
            ivDelete = itemView.findViewById(R.id.iv_note_delete);
            this.callBack = callBack;


        }

        public void bindNote(final Note note) {
            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getContent());

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onEditBtnClicked(getAdapterPosition(), note);
                }
            });
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onDeleteBtnClicked(getAdapterPosition(), note);
                }
            });


        }

        public interface NoteViewCallBack {
            void onDeleteBtnClicked(int position, Note note);

            void onEditBtnClicked(int position, Note note);
        }
    }

    public static class DeleteAllNotesViewHolder extends RecyclerView.ViewHolder {

        private Button btnDeleteAll;

        public DeleteAllNotesViewHolder(View itemView) {
            super(itemView);
            btnDeleteAll = itemView.findViewById(R.id.btn_deleteNotes_delete);
        }

        public void setButtonVisibilaty(int visibilaty) {
            btnDeleteAll.setVisibility(visibilaty);
        }

    }
}
