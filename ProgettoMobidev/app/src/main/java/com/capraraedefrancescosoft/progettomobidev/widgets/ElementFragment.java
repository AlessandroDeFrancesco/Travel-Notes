package com.capraraedefrancescosoft.progettomobidev.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.capraraedefrancescosoft.progettomobidev.models.ElementType;
import com.capraraedefrancescosoft.progettomobidev.utilities.GeoLocationUtility;

public class ElementFragment extends Fragment {

    private ElementView elementView;

    public static Fragment newInstance(Context context, Element element) {
        Bundle b = new Bundle();
        b.putSerializable("element", element);
        return Fragment.instantiate(context, ElementFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout pager = (LinearLayout) inflater.inflate(R.layout.element_fragment_pager, container, false);
        Element element = (Element) this.getArguments().getSerializable("element");
        elementView = (ElementView) pager.findViewById(R.id.fragmentElementView);
        initElementView(element);
        return pager;
    }

    private void initElementView(Element element) {
        if (element != null) {
            elementView.populateViewWithElement(element, false);
            elementView.enableShare();
            if (element.getType() == ElementType.VIDEO && getActivity() != null)
                elementView.enableExpandedElement(getActivity().findViewById(R.id.expandedVideo), getActivity().findViewById(R.id.layoutExpandedElement));
            else if (element.getType() == ElementType.IMAGE && getActivity() != null)
                elementView.enableExpandedElement(getActivity().findViewById(R.id.expandedImage), getActivity().findViewById(R.id.layoutExpandedElement));
        }
    }

    public void refreshFragment() {
        Element element = (Element) this.getArguments().getSerializable("element");
        if(element != null && elementView != null)
            initElementView(element);
    }
}
