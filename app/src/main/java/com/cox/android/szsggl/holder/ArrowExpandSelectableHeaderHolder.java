package com.cox.android.szsggl.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cox.android.szsggl.R;
import com.cox.android.szsggl.activity.TreeListActivity;
import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/15/15, modified by Szigeti Peter 2/2/16.
 */
public class ArrowExpandSelectableHeaderHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;
    private CheckBox nodeSelector;
    Context context;

    public ArrowExpandSelectableHeaderHolder(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItemHolder.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.tree_list_selectable_header, null, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (node.isSelected()) {
                    TreeListActivity activity = (TreeListActivity) context;
                    View arrow_layout = v;//node.getViewHolder().getView().findViewById(R.id.arrow_layout);
                    PrintView iconView = (PrintView) arrow_layout.findViewById(R.id.icon);
                    activity.selectedNode.setSelected(false);

                    if (node.isLeaf()) {
                        iconView.setIconText(context.getResources().getString(value.icon_leaf));
                    } else {
                        iconView.setIconText(context.getResources().getString(value.icon));
                    }
                    iconView.setIconColor(context.getResources().getColor(R.color.tree_text_color));
                    v.setBackgroundResource(R.drawable.color_grey_selector);

                    activity.selectedNode = null;
                } else {
                    TreeListActivity activity = (TreeListActivity) context;
                    View arrow_layout = null;
                    PrintView iconView = null;
                    if (activity.selectedNode != null) {
                        arrow_layout = activity.selectedNode.getViewHolder().getView().findViewById(R.id.arrow_layout);
                        iconView = (PrintView) arrow_layout.findViewById(R.id.icon);

                        if (activity.selectedNode.isLeaf()) {
                            iconView.setIconText(context.getResources().getString(value.icon_leaf));
                        } else {
                            iconView.setIconText(context.getResources().getString(value.icon));
                        }
                        iconView.setIconColor(context.getResources().getColor(R.color.tree_text_color));
                        arrow_layout.setBackgroundResource(R.drawable.color_grey_selector);

                        activity.selectedNode.setSelected(false);
                        activity.selectedNode = null;
                    }

                    node.setSelected(true);
                    activity.selectedNode = node;
                    arrow_layout = v;//activity.selectedNode.getViewHolder().getView().findViewById(R.id.arrow_layout);
                    iconView = (PrintView) arrow_layout.findViewById(R.id.icon);
                    iconView.setIconText(context.getResources().getString(R.string.ic_check_circle));
                    iconView.setIconColor(context.getResources().getColor(R.color.text_color_orange_1));
                    v.setBackgroundResource(R.drawable.border_yellow);
                }
            }
        });

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);
//        tvValue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView tv = (TextView) v;
//                node.setSelected(true);
//                tv.setBackgroundColor(context.getResources().getColor(R.color.bg_blue));
//                tv.setTextColor(context.getResources().getColor(R.color.bg_blue));
//            }
//        });

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconColor(context.getResources().getColor(R.color.tree_text_color));
        if (node.isLeaf()) {
            iconView.setIconText(context.getResources().getString(value.icon_leaf));
        } else {
            iconView.setIconText(context.getResources().getString(value.icon));
        }

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        arrowView.setPadding(20, 10, 10, 10);
        if (node.isLeaf()) {
            arrowView.setIconColor(context.getResources().getColor(R.color.transparent));
        }
        arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tView.toggleNode(node);
            }
        });

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                for (TreeNode n : node.getChildren()) {
                    getTreeView().selectNode(n, isChecked);
                }
            }
        });
        nodeSelector.setChecked(node.isSelected());

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}
