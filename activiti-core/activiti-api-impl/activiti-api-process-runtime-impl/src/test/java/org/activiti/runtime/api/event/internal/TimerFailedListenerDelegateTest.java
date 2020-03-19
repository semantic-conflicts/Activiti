/*
 * Copyright 2019 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.runtime.api.event.internal;

import org.activiti.api.process.model.events.BPMNTimerFailedEvent;
import org.activiti.api.process.runtime.events.listener.BPMNElementEventListener;
import org.activiti.api.runtime.event.impl.BPMNTimerFailedEventImpl;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.runtime.api.event.impl.ToTimerFailedConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class TimerFailedListenerDelegateTest {

    private TimerFailedListenerDelegate listenerDelegate;

    @Mock
    private BPMNElementEventListener<BPMNTimerFailedEvent> listener;

    @Mock
    private ToTimerFailedConverter converter;

    @BeforeEach
    public void setUp() {
        listenerDelegate = new TimerFailedListenerDelegate(Collections.singletonList(listener), converter);
    }

    @Test
    public void shouldCallRegisteredListenersWhenConvertedEventIsNotEmpty() {
        //given
        ActivitiEntityEvent internalEvent = mock(ActivitiEntityEvent.class);
        BPMNTimerFailedEventImpl convertedEvent = new BPMNTimerFailedEventImpl();
        given(converter.from(internalEvent)).willReturn(Optional.of(convertedEvent));

        //when
        listenerDelegate.onEvent(internalEvent);

        //then
        verify(listener).onEvent(convertedEvent);
    }

    @Test
    public void shouldDoNothingWhenConvertedEventIsEmpty() {
        //given
        ActivitiEntityEvent internalEvent = mock(ActivitiEntityEvent.class);
        given(converter.from(internalEvent)).willReturn(Optional.empty());

        //when
        listenerDelegate.onEvent(internalEvent);

        //then
        verify(listener, never()).onEvent(any());
    }

}
