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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.messic.server.api.datamodel.Author;
import org.messic.server.api.datamodel.User;
import org.messic.server.datamodel.MDOAlbum;
import org.messic.server.datamodel.MDOAuthor;
import org.messic.server.datamodel.dao.DAOAlbum;
import org.messic.server.datamodel.dao.DAOAuthor;
import org.messic.server.datamodel.dao.DAOGenre;
import org.messic.server.datamodel.dao.DAOMessicSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class APIAuthor
{
    @Autowired
    private DAOAuthor daoAuthor;

    @Autowired
    private APIAlbum apiAlbum;

    @Autowired
    private DAOGenre daoGenre;

    @Autowired
    private DAOAlbum daoAlbum;

    @Autowired
    private DAOMessicSettings daoSettings;

    @Transactional
    public void remove( User user, Long authorSid )
        throws IOException
    {
        MDOAuthor author = daoAuthor.get( user.getLogin(), authorSid );
        if ( author != null )
        {
            String path = author.calculateAbsolutePath( daoSettings.getSettings() );
            daoAuthor.remove( author );
            FileUtils.deleteDirectory( new File( path ) );
        }
    }

    @Transactional
    public List<Author> getAll( User user, boolean copyAlbums, boolean copySongs )
    {
        List<MDOAuthor> authors = daoAuthor.getAll( user.getLogin() );
        return Author.transform( authors, copyAlbums, copySongs );
    }

    @Transactional
    public Author getAuthor( User user, long authorSid, boolean copyAlbums, boolean copySongs )
    {
        MDOAuthor author = daoAuthor.get( user.getLogin(), authorSid );
        return Author.transform( author, copyAlbums, copySongs );
    }

    @Transactional
    public List<Author> findSimilar( User user, String authorName, boolean contains, boolean copyAlbums,
                                     boolean copySongs )
    {
        List<MDOAuthor> authors = daoAuthor.findSimilarAuthors( authorName, contains, user.getLogin() );
        return Author.transform( authors, copyAlbums, copySongs );
    }

}