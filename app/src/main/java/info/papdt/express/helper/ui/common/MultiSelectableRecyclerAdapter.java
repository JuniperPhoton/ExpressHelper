package info.papdt.express.helper.ui.common;

import android.view.View;
import android.view.ViewGroup;

public abstract class MultiSelectableRecyclerAdapter<VH extends MultiSelectableRecyclerAdapter.SelectableViewHolder> extends MyRecyclerViewAdapter<VH> {

	private boolean isSelecting = false;
	private boolean[] selectStates;

	private OnSelectingStateCallback mCallback;

	public MultiSelectableRecyclerAdapter(boolean useAnimation) {
		super(useAnimation);
		selectStates = new boolean[getItemCount()];
	}

	public void startSelect() {
		isSelecting = true;
		selectStates = new boolean[getItemCount()];
		this.notifyDataSetChanged();
		if (mCallback != null) {
			mCallback.onStart();
		}
	}

	public void endSelect() {
		isSelecting = false;
		selectStates = new boolean[getItemCount()];
		this.notifyDataSetChanged();
		if (mCallback != null) {
			mCallback.onEnd();
		}
	}

	public abstract boolean onItemSelect(int position);
	public abstract boolean onItemUnselect(int position);

	public boolean isSelecting() {
		return isSelecting;
	}

	public boolean[] getSelectStates() {
		return selectStates;
	}

	public void setOnSelectingStateCallback(OnSelectingStateCallback callback) {
		this.mCallback = callback;
	}

	@Override
	public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

	@Override
	public void onBindViewHolder(final VH holder, final int position) {
		if (isSelecting) {
			holder.getParentView().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (isSelecting && selectStates [position] ? onItemUnselect(position) : onItemSelect(position)) {
						holder.changeViewState(selectStates[position] = !selectStates[position],
								holder.nowState != selectStates[position]);
					}
				}
			});
			holder.getParentView().setOnLongClickListener(null);
		} else {
			super.onBindViewHolder(holder, position);
			holder.getParentView().setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					if (!isSelecting && onItemSelect(position)) {
						startSelect();
						holder.changeViewState(true, true);
						selectStates[position] = true;
					}
					return false;
				}
			});
		}
	}

	@Override
	public abstract int getItemCount();

	public abstract class SelectableViewHolder extends MyRecyclerViewAdapter.ClickableViewHolder {

		protected boolean nowState = false;

		public SelectableViewHolder(View itemView) {
			super(itemView);
		}

		public abstract void changeViewState(boolean isSelected, boolean animate);

	}

	public interface OnSelectingStateCallback {

		public void onStart();
		public void onEnd();

	}

}