package org.vaadin.addon.leaflet.client;

import org.vaadin.addon.leaflet.shared.AbstractLeafletVectorState;
import org.vaadin.addon.leaflet.shared.LeafletMarkerClientRpc;
import com.google.gwt.core.client.JsonUtils;
import org.peimari.gleaflet.client.AbstractPath;
import org.peimari.gleaflet.client.PathOptions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ServerConnector;

public abstract class AbstractLeafletVectorConnector<T extends AbstractLeafletVectorState, O extends PathOptions>
		extends AbstractLeafletLayerConnector<O> {

	AbstractLeafletVectorConnector() {
		registerRpc(LeafletMarkerClientRpc.class, new LeafletMarkerClientRpc() {

			@Override
			public void openPopup() {
				Scheduler.get().scheduleDeferred(
						new Scheduler.ScheduledCommand() {

							@Override
							public void execute() {
								getVector().openPopup();
							}
						});
			}

			@Override
			public void closePopup() {
				getVector().closePopup();
			}

		});

	}

	protected AbstractPath getVector() {
		return (AbstractPath) getLayer();
	}

	@Override
	protected O createOptions() {
		AbstractLeafletVectorState s = getState();
		O o = JsonUtils.safeEval(s.vectorStyleJson);
                
		if (s.clickable != null) {
			o.setClickable(s.clickable);
		}
		if (s.pointerEvents != null) {
			o.setPointerEvents(s.pointerEvents);
		}
		if (s.className != null) {
			o.setClassName(s.className);
		}

		final String popup = getState().popup;
		if (popup != null) {
			Scheduler.get().scheduleFinally(new ScheduledCommand() {

				@Override
				public void execute() {
					getVector().bindPopup(
							popup,
							LeafletPopupConnector
									.popupOptionsFor(getState().popupState));
				}
			});

		}

		return o;
	}
	
	@Override
	public void setParent(ServerConnector parent) {
		super.setParent(parent);
		markDirty();
	}

	@Override
	public T getState() {
		return (T) super.getState();
	}
}