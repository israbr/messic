/*
 * Copyright (C) 2013
 *
 *  This file is part of Messic.
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.messic.server.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.messic.server.Util;
import org.messic.server.api.datamodel.Album;
import org.messic.server.api.datamodel.Song;
import org.messic.server.api.datamodel.User;
import org.messic.server.api.exceptions.ResourceNotFoundMessicException;
import org.messic.server.api.exceptions.SidNotFoundMessicException;
import org.messic.server.api.tagwizard.audiotagger.AudioTaggerTAGWizardPlugin;
import org.messic.server.datamodel.MDOAlbum;
import org.messic.server.datamodel.MDOAlbumResource;
import org.messic.server.datamodel.MDOArtwork;
import org.messic.server.datamodel.MDOAuthor;
import org.messic.server.datamodel.MDOGenre;
import org.messic.server.datamodel.MDOMessicSettings;
import org.messic.server.datamodel.MDOOtherResource;
import org.messic.server.datamodel.MDOSong;
import org.messic.server.datamodel.MDOSongStatistics;
import org.messic.server.datamodel.MDOUser;
import org.messic.server.datamodel.dao.DAOAlbum;
import org.messic.server.datamodel.dao.DAOAlbumResource;
import org.messic.server.datamodel.dao.DAOAuthor;
import org.messic.server.datamodel.dao.DAOGenre;
import org.messic.server.datamodel.dao.DAOMessicSettings;
import org.messic.server.datamodel.dao.DAOPhysicalResource;
import org.messic.server.datamodel.dao.DAOPlaylist;
import org.messic.server.datamodel.dao.DAOSong;
import org.messic.server.datamodel.dao.DAOSongStatistics;
import org.messic.server.datamodel.dao.DAOUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class APIAlbum
{
    @Autowired
    private DAOMessicSettings daoSettings;

    @Autowired
    private DAOAlbum daoAlbum;

    @Autowired
    private DAOAuthor daoAuthor;

    @Autowired
    private DAOGenre daoGenre;

    @Autowired
    private DAOSong daoSong;

    @Autowired
    private DAOSongStatistics daoSongStatistics;

    @Autowired
    private DAOUser daoUser;

    @Autowired
    private DAOPlaylist daoPlaylists;

    @Autowired
    private DAOPhysicalResource daoPhysicalResource;

    @Autowired
    private DAOAlbumResource daoAlbumResource;

    @Transactional
    public void getAlbumZip( User user, Long albumSid, OutputStream os )
        throws IOException
    {

        MDOAlbum album = this.daoAlbum.getAlbum( albumSid, user.getLogin() );
        if ( album != null )
        {
            String basePath = album.calculateAbsolutePath( daoSettings.getSettings() );
            Util.zipFolder( basePath + File.separatorChar + basePath, os );
        }
        else
        {
            throw new IOException( "Album not found!" );
        }
    }

    @Transactional
    public boolean remove( User user, Long albumSid )
        throws IOException
    {

        MDOAlbum album = this.daoAlbum.getAlbum( albumSid, user.getLogin() );
        if ( album != null )
        {
            // // we should remove manually all the playlist links
            // List<MDOSong> songs = album.getSongs();
            // for ( MDOSong mdoSong : songs )
            // {
            // if ( mdoSong != null )
            // {
            // Set<MDOPlaylist> playlists = mdoSong.getPlaylists();
            // for ( MDOPlaylist mdoPlaylist : playlists )
            // {
            // List<MDOSong> psongs = mdoPlaylist.getSongs();
            // for ( int i = 0; i < psongs.size(); i++ )
            // {
            // MDOSong mdoSong2 = psongs.get( i );
            // if ( mdoSong2 != null )
            // {
            // if ( mdoSong2.getSid() == mdoSong.getSid() )
            // {
            // psongs.remove( i );
            // i = i - 1;
            // }
            // }
            // }
            // daoPlaylists.save( mdoPlaylist );
            // }
            // }
            // }

            if ( album.getAuthor().getAlbums().size() <= 1 )
            {
                // first, removing the author folder
                MDOAuthor author = album.getAuthor();
                File fpath = new File( author.calculateAbsolutePath( daoSettings.getSettings() ) );
                FileUtils.deleteDirectory( fpath );
                // after, removing the author data from database
                daoAuthor.remove( album.getAuthor() );
                return true;
            }
            else
            {
                // first, removing the album folder
                File fpath = new File( album.calculateAbsolutePath( daoSettings.getSettings() ) );
                FileUtils.deleteDirectory( fpath );
                // after, removing the album data from database
                this.daoAlbum.remove( album );
                return false;
            }
        }
        return false;
    }

    @Transactional
    public List<Album> getAll( User user, boolean authorInfo, boolean songsInfo, boolean resourcesInfo )
    {
        List<MDOAlbum> albums = this.daoAlbum.getAll( user.getLogin() );
        return Album.transform( albums, authorInfo, songsInfo, resourcesInfo );
    }

    @Transactional
    public List<Album> getAll( User user, long authorSid, boolean authorInfo, boolean songsInfo, boolean resourcesInfo )
    {
        List<MDOAlbum> albums = daoAlbum.getAll( authorSid, user.getLogin() );
        return Album.transform( albums, authorInfo, songsInfo, resourcesInfo );
    }

    @Transactional
    public List<Album> getAllOfGenre( User user, long genreSid, boolean authorInfo, boolean songsInfo,
                                      boolean resourcesInfo )
    {
        List<MDOAlbum> albums = daoAlbum.getAllOfGenre( genreSid, user.getLogin() );
        return Album.transform( albums, authorInfo, songsInfo, resourcesInfo );
    }

    @Transactional
    public Album getAlbum( User user, long albumSid, boolean authorInfo, boolean songsInfo, boolean resourcesInfo )
    {
        MDOAlbum album = daoAlbum.getAlbum( albumSid, user.getLogin() );
        return Album.transform( album, authorInfo, songsInfo, resourcesInfo );
    }

    @Transactional
    public List<Album> findSimilar( MDOUser user, String albumName, boolean authorInfo, boolean songsInfo,
                                    boolean resourcesInfo )
    {
        List<MDOAlbum> albums = daoAlbum.findSimilarAlbums( albumName, user.getLogin() );
        return Album.transform( albums, authorInfo, songsInfo, resourcesInfo );
    }

    @Transactional
    public List<Album> findSimilar( User user, int authorSid, String albumName, boolean authorInfo, boolean songsInfo,
                                    boolean resourcesInfo )
    {
        List<MDOAlbum> albums = daoAlbum.findSimilarAlbums( authorSid, albumName, user.getLogin() );
        return Album.transform( albums, authorInfo, songsInfo, resourcesInfo );
    }

    /**
     * Reset the temporal folder. It removes all the temporal files. if albumCode exists, remove only these temporal
     * files for the code album, if not, remove everything. This is useful when the user wants to upload new songs
     * 
     * @param albumCode String code for the album to reset
     * @param exceptionFiles list of files that we don't want to remove
     * @return List<File/> the list of files that are still at the temporal folder (based on the exceptionfiles
     *         parameter)
     * @throws IOException
     */
    public List<org.messic.server.api.datamodel.File> clearTemporal( User user,
                                                                     String albumCode,
                                                                     List<org.messic.server.api.datamodel.File> exceptionFiles )
        throws IOException
    {
        MDOUser mdouser = daoUser.getUserByLogin( user.getLogin() );
        File basePath = null;
        if ( albumCode != null && albumCode.length() > 0 )
        {
            basePath = new File( mdouser.calculateTmpPath( daoSettings.getSettings(), albumCode ) );
        }
        else
        {
            basePath = new File( mdouser.calculateTmpPath( daoSettings.getSettings(), "" ) );
        }

        // first, removing everything, except the current albumcode folder (if exist)
        File ftmppath = new File( mdouser.calculateTmpPath( daoSettings.getSettings(), "" ) );
        if ( ftmppath.exists() )
        {
            File[] files = ftmppath.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].isDirectory() && !files[i].getName().equals( albumCode ) )
                {
                    FileUtils.deleteDirectory( files[i] );
                }
            }
        }

        if ( basePath.exists() && ( exceptionFiles == null || exceptionFiles.size() == 0 ) )
        {
            FileUtils.deleteDirectory( basePath );
        }
        else if ( basePath.exists() && exceptionFiles != null )
        {
            deleteFiles( basePath.getAbsolutePath(), exceptionFiles );
        }

        basePath.mkdirs();

        ArrayList<File> existingFiles = new ArrayList<File>();
        Util.listFiles( basePath.getAbsolutePath(), existingFiles );

        ArrayList<org.messic.server.api.datamodel.File> result = new ArrayList<org.messic.server.api.datamodel.File>();
        for ( int i = 0; i < existingFiles.size(); i++ )
        {
            org.messic.server.api.datamodel.File f = new org.messic.server.api.datamodel.File();
            f.setFileName( existingFiles.get( i ).getName() );
            f.setSize( existingFiles.get( i ).length() );
            result.add( f );
        }
        return result;
    }

    /**
     * Check if a certainFile is an exceptionFile, it means, a file that we shouldn't remove.
     * 
     * @param f {@link File} file to check
     * @param exceptionFiles {@link List}<File/> List of exceptionFiles
     * @return boolean true->yes, its an exception file , false->No, it isn't an exception file
     */
    private boolean isAnExceptionFile( File f, List<org.messic.server.api.datamodel.File> exceptionFiles,
                                       char replacementChar )
    {
        if ( exceptionFiles == null || exceptionFiles.size() == 0 )
        {
            return false;
        }
        for ( int i = 0; i < exceptionFiles.size(); i++ )
        {
            if ( f.getName().equals( exceptionFiles.get( i ).calculateSecureFileName( replacementChar ) ) )
            {
                if ( f.length() == exceptionFiles.get( i ).getSize() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Delete all the files of a certain path, and subpaths, except those files in the list of the parameter
     * exceptionFiles
     * 
     * @param basePath {@link String} basePath to start searching
     * @param exceptionFiles List<File/> Black list we don't want to remove
     */
    private void deleteFiles( String basePath, List<org.messic.server.api.datamodel.File> exceptionFiles )
    {
        File directory = new File( basePath );
        char replacementChar = daoSettings.getSettings().getIllegalCharacterReplacement();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for ( File file : fList )
        {
            if ( file.isFile() )
            {
                if ( !isAnExceptionFile( file, exceptionFiles, replacementChar ) )
                {
                    file.delete();
                }
            }
            else if ( file.isDirectory() )
            {
                deleteFiles( file.getAbsolutePath(), exceptionFiles );
            }
        }
    }

    /**
     * Add a resource to the temporal folder. This is necessary to do after things like, wizard, or create album, ..
     * 
     * @param albumCode {@link String} album code for the resources to upload
     * @param fileName String file name uploaded
     * @param payload byte[] bytes of the track
     * @throws IOException
     * @throws Exception
     */
    public void uploadResource( User user, String albumCode, String fileName, byte[] payload )
        throws IOException
    {
        MDOUser mdouser = daoUser.getUserByLogin( user.getLogin() );
        File basePath = new File( mdouser.calculateTmpPath( daoSettings.getSettings(), albumCode ) );
        basePath.mkdirs();

        org.messic.server.api.datamodel.File f = new org.messic.server.api.datamodel.File();
        f.setFileName( fileName );

        FileOutputStream fos =
            new FileOutputStream( new File( basePath.getAbsolutePath() + File.separatorChar
                + f.calculateSecureFileName( daoSettings.getSettings().getIllegalCharacterReplacement() ) ) );
        fos.write( payload );
        fos.close();
    }

    public byte[] getAlbumResource( User mdouser, Long resourceSid )
        throws SidNotFoundMessicException, ResourceNotFoundMessicException, IOException
    {
        MDOAlbumResource resource = daoAlbumResource.get( resourceSid );
        if ( resource != null )
        {
            String resourcePath = resource.calculateAbsolutePath( daoSettings.getSettings() );
            File ftor = new File( resourcePath );
            if ( ftor.exists() )
            {
                return Util.readFile( resourcePath );
            }
            else
            {
                throw new ResourceNotFoundMessicException( resourcePath );
            }
        }
        else
        {
            throw new SidNotFoundMessicException();
        }
    }

    public void setAlbumCover( User user, Long albumSid, Long resourceSid )
    {
        MDOUser mdouser = daoUser.getUserByLogin( user.getLogin() );
        this.daoAlbum.setAlbumCover( resourceSid, albumSid, mdouser.getSid() );
    }

    public byte[] getAlbumCover( User mdouser, Long albumSid )
        throws SidNotFoundMessicException, ResourceNotFoundMessicException, IOException
    {
        MDOAlbumResource cover = daoAlbum.getAlbumCover( albumSid, mdouser.getLogin() );
        if ( cover != null )
        {
            String resourcePath = cover.calculateAbsolutePath( daoSettings.getSettings() );
            File ftor = new File( resourcePath );
            if ( ftor.exists() )
            {
                return Util.readFile( resourcePath );
            }
            else
            {
                throw new ResourceNotFoundMessicException( resourcePath );
            }
        }
        else
        {
            throw new ResourceNotFoundMessicException( "" + albumSid );
        }
    }

    public void createOrUpdateAlbum( User user, Album album )
        throws IOException
    {
        MDOGenre mdoGenre = null;
        MDOAlbum mdoAlbum = null;
        MDOAuthor mdoAuthor = null;
        MDOUser mdouser = daoUser.getUserByLogin( user.getLogin() );
        char replacementChar = daoSettings.getSettings().getIllegalCharacterReplacement();
        MDOMessicSettings settings = daoSettings.getSettings();

        // 1st getting genre ###############################################################################
        if ( album.getGenre() != null && album.getGenre().getSid() != null )
        {
            mdoGenre = daoGenre.get( album.getGenre().getSid() );
        }
        if ( mdoGenre == null )
        {
            if ( album.getGenre() != null && album.getGenre().getName() != null
                && album.getGenre().getName().trim().length() > 0 )
            {
                mdoGenre = daoGenre.getByName( user.getLogin(), album.getGenre().getName() );
            }
        }
        if ( mdoGenre == null && album.getGenre() != null && album.getGenre().getName() != null
            && album.getGenre().getName().trim().length() > 0 )
        {
            mdoGenre = new MDOGenre( album.getGenre().getName(), mdouser );
        }

        // 2nd getting the album if exist
        // ###############################################################################
        if ( album.getSid() > 0 )
        {
            mdoAlbum = daoAlbum.get( album.getSid() );
        }

        // 3rd getting the author ###############################################################################
        if ( album.getAuthor().getSid() > 0 )
        {
            // trying by sid
            mdoAuthor = daoAuthor.get( user.getLogin(), album.getAuthor().getSid() );
        }
        if ( mdoAuthor == null )
        {
            // trying by name
            mdoAuthor = daoAuthor.getByName( album.getAuthor().getName(), user.getLogin() );
        }
        if ( mdoAuthor != null )
        {
            // an existing album from this autor??
            if ( mdoAlbum == null )
            {
                mdoAlbum = daoAlbum.getByName( mdoAuthor.getName(), album.getName(), user.getLogin() );
            }
        }
        // let's create a new author
        if ( mdoAuthor == null )
        {
            mdoAuthor = new MDOAuthor();
            mdoAuthor.setName( album.getAuthor().getName() );
            mdoAuthor.setOwner( mdouser );
            mdoAuthor.setLocation( Util.replaceIllegalFilenameCharacters( album.getAuthor().getName(), replacementChar ) );
        }
        // 4th new album if none ###############################################################################
        if ( mdoAlbum == null )
        {
            mdoAlbum = new MDOAlbum();
        }

        boolean flagExistingAuthor = ( mdoAuthor.getSid() != null && mdoAuthor.getSid() > 0 );
        boolean flagAuthorNameChanged = false;
        boolean flagExistingAlbum = ( mdoAlbum.getSid() != null && mdoAlbum.getSid() > 0 );
        // old album path if the album was an existing one
        String oldAlbumPath = null;
        if ( flagExistingAlbum )
        {
            oldAlbumPath = mdoAlbum.calculateAbsolutePath( settings );
        }

        // 5th updating / creating the album
        // ###############################################################################

        // if its an existing author and the name of the author has changed...wow!! we must do more things
        if ( flagExistingAuthor && !album.getAuthor().getName().equals( mdoAuthor.getName() ) )
        {
            // the name of the author has changed!!!
            flagAuthorNameChanged = true;
            mdoAuthor.setName( album.getAuthor().getName() );
            mdoAuthor.setLocation( Util.replaceIllegalFilenameCharacters( album.getAuthor().getName(), replacementChar ) );
        }

        mdoAlbum.setName( album.getName() );
        mdoAlbum.setLocation( Util.replaceIllegalFilenameCharacters( album.getName(), replacementChar ) );
        mdoAlbum.setAuthor( mdoAuthor );
        mdoAlbum.setComments( album.getComments() );
        mdoAlbum.setGenre( mdoGenre );
        mdoAlbum.setOwner( mdouser );
        mdoAlbum.setYear( album.getYear() );

        // 6th saving author, genre and album
        // ###############################################################################
        daoAuthor.save( mdoAuthor );
        if ( mdoGenre != null )
        {
            daoGenre.save( mdoGenre );
        }
        daoAlbum.save( mdoAlbum );

        // 7th moving album resources to definitive location
        // ###############################################################################

        String currentAlbumPath = mdoAlbum.calculateAbsolutePath( settings );
        File currentAlbumPathFile = new File( currentAlbumPath );
        String userTmpPath = mdouser.calculateTmpPath( settings, album.getCode() );

        // creating album path
        currentAlbumPathFile.mkdirs();

        // 7.1 - Songs resources
        if ( album.getSongs() != null && album.getSongs().size() > 0 )
        {
            List<Song> songs = album.getSongs();
            for ( Song song : songs )
            {
                MDOSong mdoSong = new MDOSong();
                File fnew = null;

                if ( song.getSid() <= 0 )
                {
                    MDOSongStatistics ss = new MDOSongStatistics();
                    ss.setTimesplayed( 0 );
                    ss.setTimesstopped( 0 );
                    daoSongStatistics.save( ss );

                    // new song
                    mdoSong.setStatistics( ss );
                    mdoSong.setTrack( song.getTrack() );
                    mdoSong.setName( song.getName() );
                    String secureExtension = song.calculateSecureExtension( replacementChar );
                    String theoricalFileName =
                        mdoSong.calculateSongTheoricalFileName( secureExtension, replacementChar );

                    mdoSong.setLocation( theoricalFileName );
                    mdoSong.setOwner( mdouser );
                    mdoSong.setAlbum( mdoAlbum );
                    daoSong.save( mdoSong );

                    // moving resource to the new location
                    File tmpRes =
                        new File( userTmpPath + File.separatorChar + song.calculateSecureFileName( replacementChar ) );
                    fnew = new File( mdoSong.calculateAbsolutePath( settings ) );
                    if ( fnew.exists() )
                    {
                        fnew.delete();
                    }
                    FileUtils.moveFile( tmpRes, fnew );
                }
                else
                {
                    // existing song...
                    mdoSong = daoSong.get( user.getLogin(), song.getSid() );
                    if ( mdoSong != null )
                    {
                        mdoSong.setTrack( song.getTrack() );
                        mdoSong.setName( song.getName() );
                        String oldLocation = mdoSong.calculateAbsolutePath( settings );
                        String theoricalFileName =
                            mdoSong.calculateSongTheoricalFileName( mdoSong.getExtension(), replacementChar );
                        mdoSong.setLocation( theoricalFileName );
                        daoSong.save( mdoSong );

                        File fold = new File( oldLocation );
                        fnew = new File( mdoSong.calculateAbsolutePath( settings ) );
                        if ( !fold.getAbsolutePath().equals( fnew.getAbsolutePath() ) )
                        {
                            FileUtils.moveFile( fold, fnew );
                        }
                    }
                }

                AudioTaggerTAGWizardPlugin atp = new AudioTaggerTAGWizardPlugin();
                org.messic.server.api.tagwizard.service.Album salbum =
                    new org.messic.server.api.tagwizard.service.Album();
                salbum.author = mdoAlbum.getAuthor().getName();
                salbum.name = mdoAlbum.getName();
                if ( mdoAlbum.getComments() != null )
                    salbum.comments = mdoAlbum.getComments();
                if ( mdoAlbum.getGenre() != null )
                    salbum.genre = mdoAlbum.getGenre().getName();
                salbum.year = mdoAlbum.getYear();

                org.messic.server.api.tagwizard.service.Song ssong = new org.messic.server.api.tagwizard.service.Song();
                ssong.track = mdoSong.getTrack();
                ssong.name = mdoSong.getName();
                try
                {
                    atp.saveTags( salbum, ssong, fnew );
                }
                catch ( CannotReadException e )
                {
                    throw new IOException( e.getMessage(), e.getCause() );
                }
                catch ( TagException e )
                {
                    throw new IOException( e.getMessage(), e.getCause() );
                }
                catch ( ReadOnlyFileException e )
                {
                    throw new IOException( e.getMessage(), e.getCause() );
                }
                catch ( InvalidAudioFrameException e )
                {
                    throw new IOException( e.getMessage(), e.getCause() );
                }
                catch ( CannotWriteException e )
                {
                    throw new IOException( e.getMessage(), e.getCause() );
                }

            }
        }
        // 7.2 - Artwork resources
        if ( album.getArtworks() != null && album.getArtworks().size() > 0 )
        {
            List<org.messic.server.api.datamodel.File> files = album.getArtworks();
            for ( org.messic.server.api.datamodel.File file : files )
            {
                if ( file.getSid() <= 0 )
                {
                    MDOArtwork mdopr = new MDOArtwork();
                    mdopr.setLocation( file.calculateSecureFileName( replacementChar ) );
                    mdopr.setOwner( mdouser );
                    mdopr.setAlbum( mdoAlbum );

                    org.messic.server.api.datamodel.File fcover = album.getCover();
                    if ( fcover != null && file.getFileName().equals( album.getCover().getFileName() ) )
                    {
                        mdopr.setCover( true );
                    }

                    daoPhysicalResource.save( mdopr );
                    mdoAlbum.getArtworks().add( mdopr );

                    // moving resource to the new location
                    File tmpRes =
                        new File( userTmpPath + File.separatorChar + file.calculateSecureFileName( replacementChar ) );
                    File newFile =
                        new File( currentAlbumPath + File.separatorChar
                            + file.calculateSecureFileName( replacementChar ) );
                    if ( newFile.exists() )
                    {
                        newFile.delete();
                    }
                    FileUtils.moveFileToDirectory( tmpRes, currentAlbumPathFile, false );
                }
                else
                {
                    // existing artwork...
                    MDOAlbumResource resource = daoAlbumResource.get( user.getLogin(), file.getSid() );
                    if ( resource != null )
                    {
                        String oldLocation = resource.calculateAbsolutePath( settings );
                        resource.setLocation( file.calculateSecureFileName( replacementChar ) );
                        daoAlbumResource.save( resource );

                        File fold = new File( oldLocation );
                        File fnew = new File( resource.calculateAbsolutePath( settings ) );
                        if ( !fold.getAbsolutePath().equals( fnew.getAbsolutePath() ) )
                        {
                            FileUtils.moveFile( fold, fnew );
                        }
                    }
                }
            }
            daoAlbum.save( mdoAlbum );
        }

        // 7.3 - Other resources
        if ( album.getOthers() != null && album.getOthers().size() > 0 )
        {
            List<org.messic.server.api.datamodel.File> files = album.getOthers();
            for ( org.messic.server.api.datamodel.File file : files )
            {
                if ( file.getSid() <= 0 )
                {
                    MDOOtherResource mdopr = new MDOOtherResource();
                    mdopr.setLocation( file.calculateSecureFileName( replacementChar ) );
                    mdopr.setOwner( mdouser );
                    mdopr.setAlbum( mdoAlbum );
                    daoPhysicalResource.save( mdopr );
                    mdoAlbum.getOthers().add( mdopr );

                    // moving resource to the new location
                    File tmpRes =
                        new File( userTmpPath + File.separatorChar + file.calculateSecureFileName( replacementChar ) );
                    File newFile = new File( mdopr.calculateAbsolutePath( settings ) );
                    if ( newFile.exists() )
                    {
                        newFile.delete();
                    }
                    FileUtils.moveFileToDirectory( tmpRes, currentAlbumPathFile, false );
                }
                else
                {
                    // existing artwork...
                    MDOAlbumResource resource = daoAlbumResource.get( user.getLogin(), file.getSid() );
                    if ( resource != null )
                    {
                        String oldLocation = resource.calculateAbsolutePath( settings );
                        resource.setLocation( file.calculateSecureFileName( replacementChar ) );
                        daoAlbumResource.save( resource );

                        File fold = new File( oldLocation );
                        File fnew = new File( resource.calculateAbsolutePath( settings ) );
                        if ( !fold.getAbsolutePath().equals( fnew.getAbsolutePath() ) )
                        {
                            FileUtils.moveFile( fold, fnew );
                        }
                    }
                }
            }

            daoAlbum.save( mdoAlbum );
        }

        // let's see if the album was an existing one... then it should moved to the new location
        if ( oldAlbumPath != null && !oldAlbumPath.equals( currentAlbumPath ) )
        {
            List<MDOAlbumResource> resources = mdoAlbum.getAllResources();
            for ( int i = 0; i < resources.size(); i++ )
            {
                MDOAlbumResource resource = resources.get( i );
                String resourceNewPath = resource.calculateAbsolutePath( settings );
                String resourceCurrentPath = oldAlbumPath + File.separatorChar + resource.getLocation();
                File fnewPath = new File( resourceNewPath );
                File foldPath = new File( resourceCurrentPath );
                if ( foldPath.exists() )
                {
                    FileUtils.moveFile( foldPath, fnewPath );
                }
            }

            File fAlbumOldPath = new File( oldAlbumPath );
            FileUtils.deleteDirectory( fAlbumOldPath );

            if ( flagAuthorNameChanged )
            {
                File newAuthorLocation = new File( mdoAuthor.calculateAbsolutePath( settings ) );
                // we must move all the authors albums to the new one folder
                File oldAuthorFolder = fAlbumOldPath.getParentFile();
                File[] oldfiles = oldAuthorFolder.listFiles();
                for ( File file2 : oldfiles )
                {
                    if ( file2.isDirectory() )
                    {
                        FileUtils.moveDirectory( file2, newAuthorLocation );
                    }
                }

                // finally we remove the old location
                FileUtils.deleteDirectory( oldAuthorFolder );
            }
        }
    }

}
