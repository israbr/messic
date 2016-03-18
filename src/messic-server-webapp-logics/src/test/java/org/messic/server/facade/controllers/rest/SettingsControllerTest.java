package org.messic.server.facade.controllers.rest;

import org.junit.Test;
import org.messic.server.api.APIUser;
import org.messic.server.api.datamodel.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsControllerTest
{

    @Test
    public void shouldCreateUserSharingHerLibrary()
                    throws Exception
    {
        //given
        final APIUser userAPI = mock( APIUser.class );
        final SettingsController settingsController = new SettingsController( userAPI, null );
        final User userSharingHerLibrary =
                        new User( 1L, "usuarioGenresos", "", "", "user", "2345", false, false, false );

        //when
        settingsController.createOrUpdate( userSharingHerLibrary );

        //then
        verify( userAPI ).createUser( userSharingHerLibrary );
    }

    @Test
    public void shouldUpdateUserSharingHerLibrary()
                    throws Exception
    {
        //given
        final APIUser userAPI = mock( APIUser.class );
        final SettingsController settingsController = new SettingsController( userAPI, null );

        final User userSharingHerLibrary = givenAnExistingUser( userAPI );

        //when
        settingsController.createOrUpdate( userSharingHerLibrary );

        //then
        verify( userAPI ).updateUser( userSharingHerLibrary );
    }

    private User givenAnExistingUser( APIUser userAPI )
    {
        final Authentication authentication = mock( Authentication.class );
        SecurityContextHolder.getContext().setAuthentication( authentication );

        final User userSharingHerLibrary =
                        new User( 1L, "usuarioGenresos", "", "", "user", "2345", false, false, false,
                                  Collections.<User>emptyList() );

        final String userLogin = userSharingHerLibrary.getLogin();
        when( authentication.getPrincipal() ).thenReturn( "toritlla" );
        when( authentication.getName() ).thenReturn( userLogin );
        when( userAPI.getUserByLogin( userLogin ) ).thenReturn( userSharingHerLibrary );
        return userSharingHerLibrary;
    }
}