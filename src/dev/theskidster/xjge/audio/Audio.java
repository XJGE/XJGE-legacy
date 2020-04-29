package dev.theskidster.xjge.audio;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.openal.AL11.*;
import dev.theskidster.xjge.util.ErrorUtil;
import dev.theskidster.xjge.util.LogLevel;
import dev.theskidster.xjge.util.Logger;

/**
 * @author J Hoffman
 * Created: Jan 23, 2020
 */

/**
 * Provides methods to interact with the audio engines {@link Source} objects. Any methods used to extend the functionality of this class must first be defined in 
 * the {@link AudioService} interface to be available through the {@link dev.theskidster.xjge.util.ServiceLocator ServiceLocator} class.
 */
public class Audio implements AudioService {

    public static final int ALL_SOURCES = 0;
    public static final int MAX_SOURCES = 63;
    private int prevMusicSourceSample;
    private int prevMusicSourceState;
    
    private float soundMasterVolume = 1;
    private float musicMasterVolume = 1;
    
    private boolean introFinished;
    
    private String prevMusicSourceSong;
    private Source musicSource;
    private Sound currSongBody;
    private Source[] sources = new Source[MAX_SOURCES];
    
    private Map<Integer, Integer> sourceSamples = new HashMap<>();
    private Map<Integer, Integer> sourceStates  = new HashMap<>();
    private Map<Integer, String> sourceSounds   = new HashMap<>();
    
    private Map<String, Sound> sounds = new HashMap<>();
    private Map<String, Song> songs   = new HashMap<>();
    
    @Override
    public void findSourceStates() {
        if(musicSource != null) {
            for(Source source : sources) {
                sourceSamples.put(source.handle, alGetSourcei(source.handle, AL_SAMPLE_OFFSET));
                sourceStates.put(source.handle, alGetSourcei(source.handle, AL_SOURCE_STATE));
            }
            
            prevMusicSourceSample = alGetSourcei(musicSource.handle, AL_SAMPLE_OFFSET);
            prevMusicSourceState  = alGetSourcei(musicSource.handle, AL_SOURCE_STATE);
        }
    }

    @Override
    public void init() {
        //Sounds
        sounds.put("beep", new Sound("sfx_beep.ogg"));
        //TODO add more sounds.
        
        //Songs
        //TODO add songs.
        
        var prevSources = new HashMap<Integer, Source>();
        
        for(int i = 0; i < sources.length; i++) {
            if(sources[i] != null) prevSources.put(sources[i].handle, sources[i]);
            else                   sources[i] = new Source();
        }
        
        if(!prevSources.isEmpty()) {
            prevSources.forEach((handle, prevSource) -> {
                for(Source currSource : sources) {
                    if(handle == currSource.handle) {
                        currSource = new Source(
                                prevSource,
                                sounds.get(sourceSounds.get(handle)),
                                sourceSamples.get(handle),
                                sourceStates.get(handle));
                    }
                }
            });
        } else {
            for(Source source : sources) {
                sourceStates.put(source.handle, AL_INITIAL);
            }
        }
        
        if(musicSource != null) {
            musicSource = new Source(
                    musicSource,
                    songs.get(prevMusicSourceSong),
                    prevMusicSourceSample,
                    prevMusicSourceState,
                    introFinished);
        } else {
            musicSource = new Source();
        }
    }

    @Override
    public float getSoundMasterVolume() {
        return soundMasterVolume;
    }

    @Override
    public float getMusicMasterVolume() {
        return musicMasterVolume;
    }

    @Override
    public void setSoundMasterVolume(float masterVolume) {
        soundMasterVolume = masterVolume;
        
        for(Source source : sources) {
            alSourcef(source.handle, AL_GAIN, masterVolume);
        }
    }

    @Override
    public void setMusicMasterVolume(float masterVolume) {
        musicMasterVolume = masterVolume;
        alSourcef(musicSource.handle, AL_GAIN, masterVolume);
    }
    
    /**
     * Finds an available {@link Source} object to use or steals one according to whichever is playing at the lowest volume.
     * 
     * @return an available source object to use
     */
    private Source findSource() {
        Source source  = null;
        boolean search = true;
        
        for(int i = 0; search; i++) {
            if(i < MAX_SOURCES) {
                if(sources[i].getState(AL_STOPPED) || sources[i].getState(AL_INITIAL)) {
                    source = sources[i];
                    search = false;
                }
            } else {
                //We'll just fkn take it then!
                var tempSources = new HashMap<Source, Float>();
                for(Source src : sources) {
                    tempSources.put(src, alGetSourcef(src.handle, AL_GAIN));
                }
                
                //Return the source with the lowest volume.
                source = tempSources.entrySet().stream()
                        .min(Comparator.comparingDouble(Map.Entry::getValue))
                        .get()
                        .getKey();
                
                alSourceStop(source.handle);
                search = false;
            }
        }
        
        return source;
    }
    
    @Override
    public int playSound(String sound, boolean loop) {
        Source source = findSource();
        
        if(sounds.containsKey(sound)) {
            source.setSound(sounds.get(sound));
        } else {
            Logger.log(LogLevel.WARNING, "Could not find sound: \"" + sound + "\"");
            source.setSound(sounds.get("beep"));
            loop = false;
        }
        sourceSounds.put(source.handle, sound);
        
        source.setLooping(loop);
        
        alSourcePlay(source.handle);
        ErrorUtil.checkALError();
        return source.handle;
    }
    
    @Override
    public void setSourceState(int handle, int state) {
        var temp = Arrays.asList(sources);
        
        if(handle == ALL_SOURCES) {
            for(Source source : sources) {
                switch(state) {
                    case AL_PLAYING:
                        if(sourceStates.get(source.handle) == AL_PAUSED || source.getState(AL_PAUSED)) {
                            alSourcePlay(source.handle);
                        }
                        break;
                    
                    case AL_PAUSED:
                        if(sourceStates.get(source.handle) == AL_PLAYING || source.getState(AL_PLAYING)) {
                            alSourcePause(source.handle);
                        }
                        break;
                        
                    case AL_STOPPED: alSourceStop(source.handle);  break;
                }
            }
        } else {
            if(handle > 0 && handle <= MAX_SOURCES) {
                switch(state) {
                    case AL_PLAYING:
                        if(sourceStates.get(handle) == AL_PAUSED || alGetSourcei(handle, AL_SOURCE_STATE) == AL_PAUSED) {
                            alSourcePlay(handle);
                        }
                        break;
                        
                    case AL_PAUSED:
                        if(sourceStates.get(handle) == AL_PLAYING || alGetSourcei(handle, AL_SOURCE_STATE) == AL_PLAYING) {
                            alSourcePause(handle);
                        }
                        break;
                        
                    case AL_STOPPED: alSourceStop(handle);  break;
                }
            } else {
                Logger.log(LogLevel.WARNING, "Could not find source by the handle of " + handle + ".");
            }
        }
        
        ErrorUtil.checkALError();
    }
    
    @Override
    public void playMusic(String song) {
        alSourceStop(musicSource.handle);
        
        musicSource = new Source();
        
        if(songs.containsKey(song)) {
            currSongBody = songs.get(song).body;
            
            if(songs.get(song).intro != null) {
                introFinished = false;
                musicSource.queueSound(songs.get(song).intro);
                musicSource.queueSound(currSongBody);
            } else {
                introFinished = true;
                musicSource.queueSound(currSongBody);
            }
        } else {
            Logger.log(LogLevel.WARNING, "Could not find song: \"" + song + "\"");
            currSongBody = sounds.get("beep");
            musicSource.queueSound(currSongBody);
            introFinished = false;
        }
        prevMusicSourceSong = song;
        
        musicSource.setLooping(introFinished);
        
        alSourcePlay(musicSource.handle);
        ErrorUtil.checkALError();
    }

    @Override
    public void pauseMusic() {
        alSourcePause(musicSource.handle);
    }

    @Override
    public void resumeMusic() {
        alSourcePlay(musicSource.handle);
    }

    @Override
    public void stopMusic() {
        alSourceStop(musicSource.handle);
    }

    @Override
    public void checkIntroFinished() {
        if(alGetSourcei(musicSource.handle, AL_BUFFERS_PROCESSED) == 2 && !introFinished) {
            alSourceUnqueueBuffers(musicSource.handle);
            
            musicSource.queueSound(currSongBody);
            musicSource.setLooping(true);
            
            alSourcePlay(musicSource.handle);
            
            introFinished = true;
        }
    }
    
}