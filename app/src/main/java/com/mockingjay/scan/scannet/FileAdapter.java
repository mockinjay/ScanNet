package com.mockingjay.scan.scannet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import static android.R.id.progress;

/**
 * Created by mockingjay on 5/1/17.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {
    private Context mContext;
    private List<File> fileList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public FileAdapter(Context mContext, List<File> fileList) {
        this.mContext = mContext;
        this.fileList = fileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final File file = fileList.get(position);
        holder.title.setText(file.getName());

        Glide.with(mContext).load(file.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, file);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, File file) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_file, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(file));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private File fileSelected;
        private final int NUMBER_CLUSTERS = 4;

        public MyMenuItemClickListener(File file) {
            fileSelected = file;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.classify:
                    final ProgressDialog dialog = ProgressDialog.show(mContext, "Classifying", "Please wait...", true);
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            KMeansClusterer kmeans = new KMeansClusterer();
                            int dimension = kmeans.readData(fileSelected);
                            ArrayList<Point> data = kmeans.getData();
                            Intent intent = new Intent(mContext, Plot.class);
                            intent.putExtra("data", data);
                            double wcss = kmeans.kMeansCluster(NUMBER_CLUSTERS);
                            if (dialog != null) dialog.dismiss();
                            mContext.startActivity(intent);
//                            Toast.makeText(mContext, fileSelected.getName(), Toast.LENGTH_SHORT).show();

                        }
                    }).start();



                    return true;
                default:
            }
            return false;
        }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
