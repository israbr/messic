package org.messic.server.datamodel.datasource;

import java.io.File;

import org.apache.commons.dbcp.BasicDataSource;

public class MessicDataSource
    extends BasicDataSource
{

    @Override
    public synchronized void setUrl( String url )
    {
        File f = new File( "./db" );
        f.mkdirs();
        String messic_path = f.getAbsolutePath();
        String newURL = url.replaceAll( "MESSIC_PATH", messic_path );
        super.setUrl( newURL );
    }

}