/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v3.endpoint;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Base class for test with <code>@Mock</code> ({@link org.mockito.Mock}) and <code>@InjectMocks</code>({@link org.mockito.InjectMocks})
 * 
 * @author afelice
 */
public class WithMocksTestCase {

    @Before 
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
