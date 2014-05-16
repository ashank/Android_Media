package com.example.demo_videoplay;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class VideoPlayFragement extends Fragment implements Callback ,OnCompletionListener, OnPreparedListener, OnBufferingUpdateListener, OnInfoListener, OnErrorListener, OnVideoSizeChangedListener{
	
	private View view;
	private String TAG="LOG";
	
	//预览类
	private SurfaceView mPreview;
	private MediaPlayer mMediaPlayer;
	private SurfaceHolder mHolder;
	private RelativeLayout  dialogFrameLayout;
	
	private int swidth;//视频的宽度；
	private int sheight;//视频的高度
	
	/*设置视频源的路径,*/
	private String path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES) +"/fanbingbing.mp4";
	
	private boolean isCompletion=false;
	
	private Timer timer=new Timer();
	private Timer timer2=new Timer();
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				dialogFrameLayout.setVisibility(View.VISIBLE);
				break;
			case 1:
				dialogFrameLayout.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.fragement_videoplay, null);
		Log.i(TAG, "---->VideoplayFragement   onCreateView()");
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   onActivityCreated()");
		
		//连接预览和开始预览
		mPreview=(SurfaceView)view.findViewById(R.id.video_preview);
		dialogFrameLayout=(RelativeLayout)view.findViewById(R.id.dialog);
		handler.sendEmptyMessage(1);
		mHolder=mPreview.getHolder();
		mHolder.addCallback(this);
		
		/*实现循环播放*/
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isCompletion) {
					prepareMedia();
					isCompletion=false;
					Log.v(TAG, "循环播放成功=="+isCompletion);
				}
			}
		}, 30000, 5000);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	
	
	private void prepareMedia() {
		// TODO Auto-generated method stub
		try{
			if (mMediaPlayer == null) {
				mMediaPlayer=new MediaPlayer();
			}else {
				 mMediaPlayer.stop();
	             mMediaPlayer.reset();
		     }
			//重置
			mMediaPlayer.reset();
			/*准备播放时*/
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnVideoSizeChangedListener(this);
			/*网络流媒体缓冲监听*/
			mMediaPlayer.setOnBufferingUpdateListener(this);
			/*警告信息监听事件*/
			mMediaPlayer.setOnInfoListener(this);
			/*错误信息监听事件*/
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			/*显示视频*/
			if (mHolder==null) {
				mHolder=mPreview.getHolder();
			}
			mMediaPlayer.setDisplay(mHolder);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			mMediaPlayer.setDataSource(path);
			
			mMediaPlayer.prepareAsync();
			/*循环播放*/
			mMediaPlayer.setLooping(true);
			/*播放时屏幕保持唤醒*/
			mMediaPlayer.setScreenOnWhilePlaying(true);
		} catch (IllegalArgumentException e) {
			Log.v(TAG, "VideoPlay  playVideo  IllegalArgumentException"+e.getMessage());
		} catch (SecurityException e) {
			Log.v(TAG, "VideoPlay  playVideo  SecurityException"+e.getMessage());
		} catch (IllegalStateException e) {
			Log.v(TAG, "VideoPlay  playVideo  IllegalStateException"+e.getMessage());
		}
		catch (IOException e) {
			Log.v(TAG, "VideoPlay  playVideo  IOException"+e.getMessage());
		}
		catch (Exception e) {
			Log.v(TAG, "VideoPlay  playVideo  Exception"+e.getMessage());
		}
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		if (mMediaPlayer!=null) {
			mMediaPlayer.setDisplay(holder);
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "---->VideoplayFragement   surfaceCreated()");
		this.mHolder=holder;
	   if (mHolder == null) {
//         not ready for playback just yet, will try again later
          return;
        }
		   prepareMedia();
		   
		}
	
	@Override
	public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		mMediaPlayer.setDisplay(mHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		  // after we return from this we can't use the surface any more
        mHolder = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
	}

	

	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.v(TAG, "--------------->OnPrepare");
		/*播放前将缓冲界面去掉*/
		handler.sendEmptyMessage(1);
		swidth=mMediaPlayer.getVideoWidth();
		sheight=mMediaPlayer.getVideoHeight();
    	Log.v(TAG, "视频宽度=="+swidth+"视" +"高度=="+sheight );
    	
		mMediaPlayer.start();
	}

	/*网络缓冲过程*/
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
//		Log.v(TAG, "-onBufferingUpdate  当前播放进度"+mMediaPlayer.getCurrentPosition()+"总视频长度"+mMediaPlayer.getDuration());
	}
	
	/**
	 * 警告触发
	 */
	public boolean onInfo(MediaPlayer mp , int whatinfo, int extra) {
		
		// TODO Auto-generated method stub
		Log.v(TAG, "------------->OnInfo");
		switch (whatinfo) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			//音频和视频数据不正确地交错时将出现该提示信息.在一个  
            //正确交错的媒体文件中,音频和视频样本将依序排列,从而  
            //使得播放可以有效和平稳地进行  
			Log.e(TAG, "音频和视频数据不正确地交错时");
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			/*MediaPlayer在缓冲完后继续播放。*/
			Toast.makeText(getActivity(), "缓冲足够 ，开始播放", Toast.LENGTH_LONG).show();
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			/*视频开始播放的时候开始调用*/
			Toast.makeText(getActivity(), "暂停播放，准备更多数据", Toast.LENGTH_LONG).show();
			break;
		case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
			break;
		case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
			Toast.makeText(getActivity(), "媒体不支持seek", Toast.LENGTH_LONG).show();
			break;
		case MediaPlayer.MEDIA_INFO_UNKNOWN:
			Toast.makeText(getActivity(), "播放错误，未知错误", Toast.LENGTH_LONG).show();
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
			//当设备无法播放视频时将出现该提示信息  
            //比如视频太复杂或者码率过高 
			Log.e(TAG, "无法解码");
			break;
		default:
			Log.e(TAG, "调用info"+whatinfo+"   "+extra);
			break;
		}
		return false;
	}

	public boolean onError(MediaPlayer mp, int whaterror, int extra) {
		// TODO Auto-generated method stub
		mMediaPlayer.reset();
		Log.v(TAG, "------------->OnError");
		Log.d(TAG, "whaterror===" + String.valueOf(whaterror) + "  Extra===" + String.valueOf(extra));
		switch (whaterror) {
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
			Toast.makeText(getActivity(), "播放错误，视频未知错误", Toast.LENGTH_LONG).show();
			break;
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			Toast.makeText(getActivity(), "播放错误，媒体的后台服务出错。", Toast.LENGTH_LONG).show();
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			Toast.makeText(getActivity(), "播放错误，未知错误", Toast.LENGTH_LONG).show();
			break;
		case -38:
			Toast.makeText(getActivity(), "无法播放您的视频", Toast.LENGTH_LONG).show();
			break;
		}
		return false;
	}
	
	/*播放完完成时的事件*/
	public void onCompletion(MediaPlayer mp) {
		isCompletion=true;
		/*if (mMediaPlayer != null) {
			mMediaPlayer.start();
			mMediaPlayer.seekTo(0);
			Log.v(TAG, "完成后，重新播放");
		}*/
		Log.v(TAG, "播放完成");
		Toast.makeText(getActivity(), "播放完成", Toast.LENGTH_LONG).show();
	}
	

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnAttach()");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnCreate()");
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		timer.cancel();
		timer2.cancel();
		Log.i(TAG, "---->VideoplayFragement   OnDestroy()");
		if (null!=mMediaPlayer) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer=null;
		}
		mHolder=null;
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnDestoryView()");
		timer.cancel();
		timer2.cancel();
		if (null!=mMediaPlayer) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer=null;
		}
		mHolder=null;
		super.onDestroyView();
	}
	

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnPause()");
		if (null!=mMediaPlayer) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer=null;
		}
		mHolder=null;
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnResume()");
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnStart()");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i(TAG, "---->VideoplayFragement   OnStop()");
		super.onStop();
	}

	
}
