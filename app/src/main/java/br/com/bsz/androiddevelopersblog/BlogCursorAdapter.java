package br.com.bsz.androiddevelopersblog;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.bsz.androiddevelopersblog.data.BlogContract;
import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by diegobezerrasouza on 21/04/15.
 */
public class BlogCursorAdapter extends CursorAdapter {

    private Context context;
    private Cursor swapCursor;

    public BlogCursorAdapter(Context context) {
        super(context, null, false);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.blog_content_item, null);
        view.setTag(getViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final String DATE_FORMAT = "dd/MM/yyyy";

        String title = cursor.getString(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_TITLE));
        Date published = new Date(cursor.getLong(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_PUBLISHED)));
        String mediaThumbURL = cursor.getString(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_MEDIA_THUMB));
        String blogId = cursor.getString(cursor.getColumnIndex(BlogContract.ArticleEntry.COLUMN_BLOG_ID));

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.getMediaThumb().setImageResource(R.mipmap.ic_launcher);
        if (!mediaThumbURL.isEmpty() && !mediaThumbURL.contains(".gif")) {
            Picasso.with(context).load(mediaThumbURL).into(viewHolder.getMediaThumb());
        }
        //BitmapMemoryCache.loadBitmap(context, blogId, mediaThumbURL, viewHolder.getMediaThumb());
        viewHolder.getTitle().setText(title);
        viewHolder.getPublished().setText(new SimpleDateFormat(DATE_FORMAT, Locale.US).format(published));
    }

    private ViewHolder getViewHolder(View view) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.setMediaThumb((ImageView) view.findViewById(R.id.mediaThumb));
        viewHolder.setTitle((TextView) view.findViewById(R.id.title));
        viewHolder.setPublished((TextView) view.findViewById(R.id.published));

        return viewHolder;
    }

    private class ViewHolder {

        private ImageView mediaThumb;
        private TextView published;
        private TextView title;

        public ImageView getMediaThumb() {
            return mediaThumb;
        }

        public void setMediaThumb(ImageView mediaThumb) {
            this.mediaThumb = mediaThumb;
        }

        public TextView getPublished() {
            return published;
        }

        public void setPublished(TextView published) {
            this.published = published;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }
    }
}
