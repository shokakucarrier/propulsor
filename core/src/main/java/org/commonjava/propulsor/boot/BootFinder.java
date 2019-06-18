/**
 * Copyright (C) 2015 John Casey (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.propulsor.boot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.IOUtils;

public class BootFinder
{

    public static BootInterface find()
                    throws BootException
    {
        return find( Thread.currentThread()
                           .getContextClassLoader() );
    }

    public static BootInterface find( final ClassLoader classloader )
                    throws BootException
    {
        final InputStream stream =
                        classloader.getResourceAsStream( "META-INF/services/" + BootInterface.class.getName() );
        if ( stream == null )
        {
            throw new BootException( "No BootInterface implementations registered." );
        }

        List<String> lines;
        try
        {
            lines = IOUtils.readLines( stream );
        }
        catch ( final IOException e )
        {
            throw new BootException( "Failed to read registration of BootInterface: " + e.getMessage(), e );
        }

        final String className = lines.get( 0 );
        try
        {
            final Class<?> cls = classloader.loadClass( className );
            return (BootInterface) cls.newInstance();
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException e )
        {
            throw new BootException( "Failed to initialize BootInterface: %s. Reason: %s", e, className,
                                         e.getMessage() );
        }

        /*ServiceLoader<BootInterface> bootInterfaces = ServiceLoader.load( BootInterface.class );

        for ( BootInterface cp : bootInterfaces )
        {
            return cp;
        }
        return null;*/
    }

}
