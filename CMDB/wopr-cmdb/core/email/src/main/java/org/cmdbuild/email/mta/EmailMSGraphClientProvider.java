/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.microsoft.graph.requests.GraphServiceClient;

/**
 *
 * @author afelice
 */
public interface EmailMSGraphClientProvider extends AutoCloseable {

    GraphServiceClient create();

    @Override
    void close();

}
