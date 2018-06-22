package com.capraraedefrancescosoft.progettomobidev.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capraraedefrancescosoft.progettomobidev.models.Journals;
import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.ElementType;
import com.capraraedefrancescosoft.progettomobidev.utilities.FontUtility;
import com.capraraedefrancescosoft.progettomobidev.utilities.GeoLocationUtility;
import com.capraraedefrancescosoft.progettomobidev.utilities.ListenerUtility;

import java.text.SimpleDateFormat;

/**
 * Created by Ianfire on 27/09/2016.
 */
public class ElementView extends RelativeLayout implements View.OnClickListener {

    private static final SimpleDateFormat HOUR_MINUTES_FORMAT = new SimpleDateFormat("HH:mm");

    private Element element;

    private View root;
    private ViewGroup elementContainer, placeButton;
    private ImageButton shareButton;
    private TextView ownerName, time, place;
    private ProgressBar progressBarDownloadingElement;
    private View elementView, expandedElementView;

    private AnimatorSet mCurrentAnimator;
    private boolean editable;
    private View expandedContainer;

    public ElementView(Context context) {
        super(context);
        init(context, null);
    }

    public ElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ElementView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.root = inflate(context, R.layout.element_view, this);
        this.elementContainer = (ViewGroup) findViewById(R.id.element_container);
        this.placeButton = (ViewGroup) findViewById(R.id.placeButton);
        this.shareButton = (ImageButton) findViewById(R.id.imageButtonShare);
        this.ownerName = (TextView) findViewById(R.id.textViewName);
        this.time = (TextView) findViewById(R.id.textViewTime);
        this.place = (TextView) findViewById(R.id.textViewPlace);
        this.progressBarDownloadingElement = (ProgressBar) findViewById(R.id.progressBarDownloadingElement);
    }

    public void populateViewWithElement(Element element, boolean editable) {
        this.editable = editable;
        this.element = element;
        // popolo il view holder
        ownerName.setText(element.getOwnerName());
        time.setText(HOUR_MINUTES_FORMAT.format(element.getDate()));
        GeoLocationUtility.getInstance().getLocation(getContext(), element.getLatitude(), element.getLongitude(), new GeoLocationUtility.LocationFoundCallback() {
            @Override
            public void onLocationFound(final String location) {
                place.setText(location);
                System.out.println("Settata location: " + location);
            }
        });
        placeButton.setOnClickListener(ListenerUtility.getPlaceOnClickListener(getContext(), element, Journals.getInstance().getCurrentJournal()));
        // nascondo di default lo share
        shareButton.setVisibility(INVISIBLE);
        // setto i font
        ownerName.setTypeface(FontUtility.getInstance(getContext()).getFont(FontUtility.FontType.TITLE));
        place.setTypeface(FontUtility.getInstance(getContext()).getFont(FontUtility.FontType.EXTRA));
        time.setTypeface(FontUtility.getInstance(getContext()).getFont(FontUtility.FontType.EXTRA));

        inflateElementView(element, (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        // qui popolo l'elementContainer a seconda del tipo
        if (element.getContent() != null) {
            setContent(element.getContent());
        }
    }

    public void setContent(String content) {
        progressBarDownloadingElement.setVisibility(GONE);
        switch (element.getType()) {
            case NOTE:
                ((TextView) elementView).setText(content);
                break;
            case IMAGE:
                Uri uri = Uri.parse(content);
                ((ImageView) elementView).setImageURI(uri);
                break;
            case VIDEO:
                Uri uri2 = Uri.parse(content);
                ((VideoPlayerView) elementView).loadVideo(uri2);
                break;
        }
    }

    public void enableShare() {
        shareButton.setVisibility(VISIBLE);
        shareButton.setOnClickListener(ListenerUtility.getSharingOnClickListener(getContext(), element));
    }

    public void enableExpandedElement(View expandedElementView, View expandedContainer) {
        this.expandedContainer = expandedContainer;
        this.expandedElementView = expandedElementView;

        View expandButton = root.findViewById(R.id.expandButton);
        expandButton.setVisibility(VISIBLE);
        expandButton.setOnClickListener(this);
    }

    public void disablePlaceButton(){
        placeButton.setClickable(false);
    }

    private void inflateElementView(Element element, LayoutInflater vi) {
        // elimino il vecchio contenuto dell'element container se presente
        elementContainer.removeAllViews();
        // faccio l'inflate della giusta view a seconda del tipo
        switch (element.getType()) {
            case NOTE:
                if (editable)
                    elementView = vi.inflate(R.layout.element_note_editable, elementContainer).findViewById(R.id.element);
                else
                    elementView = vi.inflate(R.layout.element_note, elementContainer).findViewById(R.id.element);
                // la barra di caricamento per la nota e' inutile
                ((TextView) elementView).setTypeface(FontUtility.getInstance(getContext()).getFont(FontUtility.FontType.NOTE));
                progressBarDownloadingElement.setVisibility(GONE);
                break;
            case IMAGE:
                elementView = vi.inflate(R.layout.element_image, elementContainer).findViewById(R.id.element);
                // nascondo di default il pulsante per l'espansione
                root.findViewById(R.id.expandButton).setVisibility(INVISIBLE);
                break;
            case VIDEO:
                elementView = vi.inflate(R.layout.element_video, elementContainer).findViewById(R.id.element);
                // nascondo di default il pulsante per l'espansione
                root.findViewById(R.id.expandButton).setVisibility(INVISIBLE);
                break;
        }
    }

    public void zoomElementOnClick() {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        if (element.getType() == ElementType.IMAGE)
            ((ImageView) expandedElementView).setImageURI(Uri.parse(element.getContent()));
        else
            ((VideoPlayerView) expandedElementView).loadVideo(Uri.parse(element.getContent()));

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        elementView.getGlobalVisibleRect(startBounds);
        expandedElementView.getGlobalVisibleRect(finalBounds, globalOffset);

        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        elementView.setAlpha(0f);
        expandedElementView.setVisibility(View.VISIBLE);
        expandedContainer.setVisibility(VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedElementView.setPivotX(0f);
        expandedElementView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedElementView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedElementView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedElementView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedElementView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(250);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                if (element.getType() == ElementType.VIDEO) {
                    ((VideoPlayerView) expandedElementView).loadAndStartVideo(Uri.parse(element.getContent()));
                    ((VideoPlayerView) elementView).pausePlay();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        OnClickListener closeExpandedView = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                if (element.getType() == ElementType.VIDEO)
                    ((VideoPlayerView) expandedElementView).stopPlay();

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedElementView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedElementView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedElementView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedElementView, View.SCALE_Y, startScaleFinal));
                set.setDuration(250);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        elementView.setAlpha(1f);
                        expandedElementView.setVisibility(View.INVISIBLE);
                        expandedContainer.setVisibility(INVISIBLE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        elementView.setAlpha(1f);
                        expandedElementView.setVisibility(View.INVISIBLE);
                        expandedContainer.setVisibility(INVISIBLE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        };

        expandedContainer.setOnClickListener(closeExpandedView);
        expandedElementView.setOnClickListener(closeExpandedView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.expandButton) {
            zoomElementOnClick();
            System.out.println("Expand element");
        }
    }

    public void setError(String string) {
        if(element.getType() == ElementType.NOTE){
            ((TextView) elementView).setError(string);
        }
    }

    public String getText() {
        if(element.getType() == ElementType.NOTE){
            return ((TextView) elementView).getText().toString();
        }

        return null;
    }

    public void setLocation(Location location) {
        GeoLocationUtility.getInstance().getLocation(getContext(), (float) location.getLatitude(), (float) location.getLongitude(), new GeoLocationUtility.LocationFoundCallback() {
            @Override
            public void onLocationFound(final String location) {
                place.setText(location);
            }
        });
    }
}
