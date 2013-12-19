/**
 * Copyright 2012 Lyncode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lyncode.jtwig.functions;

import com.lyncode.jtwig.functions.exceptions.FunctionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static com.lyncode.jtwig.util.LocalThreadHolder.getApplicationContext;
import static com.lyncode.jtwig.util.LocalThreadHolder.getServletRequest;
import static java.util.Arrays.copyOfRange;

public class Translate implements Function {
    @Override
    public Object execute(Object... arguments) throws FunctionException {
        ApplicationContext applicationContext = getApplicationContext();
        MessageSource messageSource = getMessageSource(applicationContext);

        if (arguments.length < 1) throw new FunctionException("Expecting at least one argument");
        else {
            HttpServletRequest request = getServletRequest();
            Locale locale = getLocaleResolver(applicationContext).resolveLocale(request);
            Object[] parameters = copyOfRange(arguments, 1, arguments.length);

            return messageSource.getMessage(String.valueOf(arguments[0]), parameters, locale);
        }
    }

    private LocaleResolver getLocaleResolver (ApplicationContext applicationContext) {
        return applicationContext.getBean(LocaleResolver.class);
    }

    private MessageSource getMessageSource(ApplicationContext applicationContext) {
        return applicationContext.getBean(MessageSource.class);
    }
}