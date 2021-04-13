package org.briarproject.briar.android.socialbackup.recover;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;

import org.briarproject.bramble.api.Pair;
import org.briarproject.bramble.api.lifecycle.IoExecutor;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.system.AndroidExecutor;
import org.briarproject.briar.android.contact.add.nearby.QrCodeUtils;
import org.briarproject.briar.android.viewmodel.LiveEvent;
import org.briarproject.briar.android.viewmodel.MutableLiveEvent;
import org.briarproject.briar.api.socialbackup.recovery.SecretOwnerTask;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.Context.WIFI_SERVICE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

@NotNullByDefault
class OwnerReturnShardViewModel extends AndroidViewModel implements SecretOwnerTask.Observer {

	private static final Logger LOG =
			getLogger(OwnerReturnShardViewModel.class.getName());

	@SuppressWarnings("CharsetObjectCanBeUsed") // Requires minSdkVersion >= 19
	private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

//	private ReturnShardPayload returnShardPayload;

	private final AndroidExecutor androidExecutor;
	private final Executor ioExecutor;
	private final SecretOwnerTask task;

	private final MutableLiveEvent<Boolean> showQrCodeFragment =
			new MutableLiveEvent<>();
	private final MutableLiveData<SecretOwnerTask.State> state =
			new MutableLiveData<>();
	private boolean wasContinueClicked = false;
	private boolean isActivityResumed = false;
	private Bitmap qrCodeBitmap;
	private WifiManager wifiManager;

	@Inject
	OwnerReturnShardViewModel(Application app,
			AndroidExecutor androidExecutor,
			SecretOwnerTask task,
			@IoExecutor Executor ioExecutor) {
		super(app);
		this.androidExecutor = androidExecutor;
		this.ioExecutor = ioExecutor;
		this.task = task;
		wifiManager = (WifiManager) app.getSystemService(WIFI_SERVICE);

//		IntentFilter filter = new IntentFilter(ACTION_SCAN_MODE_CHANGED);
		startListening();
	}

	private InetAddress getWifiIpv4Address() {
		if (wifiManager == null) return null;
		// If we're connected to a wifi network, return its address
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info != null && info.getIpAddress() != 0) {
			return intToInetAddress(info.getIpAddress());
		}
		return null;
	}

	private InetAddress intToInetAddress(int ip) {
		byte[] ipBytes = new byte[4];
		ipBytes[0] = (byte) (ip & 0xFF);
		ipBytes[1] = (byte) ((ip >> 8) & 0xFF);
		ipBytes[2] = (byte) ((ip >> 16) & 0xFF);
		ipBytes[3] = (byte) ((ip >> 24) & 0xFF);
		try {
			return InetAddress.getByAddress(ipBytes);
		} catch (UnknownHostException e) {
			// Should only be thrown if address has illegal length
			throw new AssertionError(e);
		}
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		stopListening();
	}

	@UiThread
	void onContinueClicked() {
		wasContinueClicked = true;
		startShardReturn();
	}

	@UiThread
	void startShardReturn() {
		// If we return to the intro fragment, the continue button needs to be
		// clicked again before showing the QR code fragment
		wasContinueClicked = false;
		// If we return to the intro fragment, we may need to enable wifi and
//		hasEnabledWifi = false;
		showQrCodeFragment.setEvent(true);
	}

	@UiThread
	private void startListening() {
		task.start(this, getWifiIpv4Address());
//		KeyAgreementTask oldTask = task;
//		KeyAgreementTask newTask = keyAgreementTaskProvider.get();
//		task = newTask;
//		ioExecutor.execute(() -> {
//			if (oldTask != null) oldTask.stopListening();
//			newTask.listen();
//		});
	}

	@UiThread
	private void stopListening() {
		task.cancel();
//		KeyAgreementTask oldTask = task;
//		ioExecutor.execute(() -> {
//			if (oldTask != null) oldTask.stopListening();
//		});
	}

//	@Override
//	public void eventOccurred(Event e) {
//		if (e instanceof TransportStateEvent) {
//			TransportStateEvent t = (TransportStateEvent) e;
//			if (t.getTransportId().equals(BluetoothConstants.ID)) {
//				if (LOG.isLoggable(INFO)) {
//					LOG.info("Bluetooth state changed to " + t.getState());
//				}
//				showQrCodeFragmentIfAllowed();
//			} else if (t.getTransportId().equals(LanTcpConstants.ID)) {
//				if (LOG.isLoggable(INFO)) {
//					LOG.info("Wifi state changed to " + t.getState());
//				}
//				showQrCodeFragmentIfAllowed();
//			}
//		} else if (e instanceof KeyAgreementListeningEvent) {
//			LOG.info("KeyAgreementListeningEvent received");
//			KeyAgreementListeningEvent event = (KeyAgreementListeningEvent) e;
//			onLocalPayloadReceived(event.getLocalPayload());
//		} else if (e instanceof KeyAgreementWaitingEvent) {
//			LOG.info("KeyAgreementWaitingEvent received");
//			state.setValue(new ReturnShardState.KeyAgreementWaiting());
//		} else if (e instanceof KeyAgreementStartedEvent) {
//			LOG.info("KeyAgreementStartedEvent received");
//			state.setValue(new ReturnShardState.KeyAgreementStarted());
//		} else if (e instanceof KeyAgreementFinishedEvent) {
//			LOG.info("KeyAgreementFinishedEvent received");
//			KeyAgreementResult result =
//					((KeyAgreementFinishedEvent) e).getResult();
//			startContactExchange(result);
//			state.setValue(new ReturnShardState.SocialBackupExchangeStarted());
//		} else if (e instanceof KeyAgreementAbortedEvent) {
//			LOG.info("KeyAgreementAbortedEvent received");
//			resetPayloadFlags();
//			state.setValue(new ReturnShardState.Failed());
//		} else if (e instanceof KeyAgreementFailedEvent) {
//			LOG.info("KeyAgreementFailedEvent received");
//			resetPayloadFlags();
//			state.setValue(new ReturnShardState.Failed());
//		}
//	}

	@UiThread
//	private void startContactExchange(KeyAgreementResult result) {
//		TransportId t = result.getTransportId();
//		DuplexTransportConnection conn = result.getConnection();
//		SecretKey masterKey = result.getMasterKey();
//		boolean alice = result.wasAlice();
//		ioExecutor.execute(() -> {
//			try {
//				if (sending) {
//					socialBackupExchangeManager.sendReturnShard(conn, masterKey, alice, returnShardPayload);
//				} else {
//					returnShardPayload = socialBackupExchangeManager.receiveReturnShard(conn, masterKey, alice);
//				}
//				ReturnShardState.SocialBackupExchangeResult.Success
//						success =
//						new ReturnShardState.SocialBackupExchangeResult.Success();
//				state.postValue(
//						new ReturnShardState.SocialBackupExchangeFinished(success));
//			} catch (ContactExistsException e) {
//				tryToClose(conn);
//				ReturnShardState.SocialBackupExchangeResult.Error
//						error = new ReturnShardState.SocialBackupExchangeResult.Error(
//						e.getRemoteAuthor());
//				state.postValue(
//						new ReturnShardState.SocialBackupExchangeFinished(error));
//			} catch (DbException | IOException e) {
//				tryToClose(conn);
//				logException(LOG, WARNING, e);
//				ReturnShardState.SocialBackupExchangeResult.Error
//						error =
//						new ReturnShardState.SocialBackupExchangeResult.Error(null);
//				state.postValue(
//						new ReturnShardState.SocialBackupExchangeFinished(error));
//			}
//		});
//	}


	/**
	 * Set to true in onPostResume() and false in onPause(). This prevents the
	 * QR code fragment from being shown if onRequestPermissionsResult() is
	 * called while the activity is paused, which could cause a crash due to
	 * https://issuetracker.google.com/issues/37067655.
	 * TODO check if this is still happening with new permission requesting
	 */
	void setIsActivityResumed(boolean resumed) {
		isActivityResumed = resumed;
		// Workaround for
		// https://code.google.com/p/android/issues/detail?id=190966
//		showQrCodeFragmentIfAllowed();
	}

	LiveEvent<Boolean> getShowQrCodeFragment() {
		return showQrCodeFragment;
	}

	LiveData<SecretOwnerTask.State> getState() {
		return state;
	}

	public Bitmap getQrCodeBitmap() {
		LOG.info("getting qrCodeBitmap");
		return qrCodeBitmap;
	}

	@Override
	public void onStateChanged(SecretOwnerTask.State state) {
       if (state instanceof SecretOwnerTask.State.Listening) {
	       DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
	       ioExecutor.execute(() -> {
		       byte[] payloadBytes = ((SecretOwnerTask.State.Listening) state).getLocalPayload();
		       if (LOG.isLoggable(INFO)) {
			       LOG.info("Local payload is " + payloadBytes.length
					       + " bytes");
		       }
		       // Use ISO 8859-1 to encode bytes directly as a string
		       String content = new String(payloadBytes, ISO_8859_1);
		       qrCodeBitmap = QrCodeUtils.createQrCode(dm, content);
	       });
       }
	}
}
