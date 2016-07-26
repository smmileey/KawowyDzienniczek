package pl.kawowydzienniczek.kawowydzienniczek;


import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import pl.kawowydzienniczek.kawowydzienniczek.Constants.GeneralConstants;
import pl.kawowydzienniczek.kawowydzienniczek.Services.GeneralUtilMethods;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeneralUtilMethodsTest {

    @Test
    public void testShowProgress_mainViewSettingListenerWhenDisplayOnAndAnimationsAvailable() throws Exception {
        Context context = mock(Context.class,RETURNS_DEEP_STUBS);
        View view = mock(View.class,RETURNS_DEEP_STUBS);
        View progressBar = mock(ProgressBar.class,RETURNS_DEEP_STUBS);
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 123);

        GeneralUtilMethods generalUtilMethods = new GeneralUtilMethods(context);
        when(context.getResources().getInteger(android.R.integer.config_shortAnimTime)).thenReturn(200);

        generalUtilMethods.showProgress(true, view, progressBar);
        verify(view).setVisibility(View.GONE);
        verify(view.animate().setDuration(anyLong()).alpha(anyFloat())).setListener(isA(Animator.AnimatorListener.class));
    }

    @Test
    public void testShowProgress_progressBarViewSettingListenerWhenDisplayOnAndAnimationsAvailable() throws Exception {
        Context context = mock(Context.class,RETURNS_DEEP_STUBS);
        View view = mock(View.class,RETURNS_DEEP_STUBS);
        View progressBar = mock(ProgressBar.class,RETURNS_DEEP_STUBS);
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 123);

        GeneralUtilMethods generalUtilMethods = new GeneralUtilMethods(context);
        when(context.getResources().getInteger(android.R.integer.config_shortAnimTime)).thenReturn(200);

        generalUtilMethods.showProgress(true, view, progressBar);
        verify(progressBar).setVisibility(View.VISIBLE);
        verify(progressBar.animate().setDuration(anyLong()).alpha(anyFloat())).setListener(isA(Animator.AnimatorListener.class));
    }

    @Test
    public void testShowProgress_SettingListenerWhenDisplayOnAndAnimationsNotAvailable() {
        Context context = mock(Context.class,RETURNS_DEEP_STUBS);
        View view = mock(View.class,RETURNS_DEEP_STUBS);
        View progressBar = mock(ProgressBar.class,RETURNS_DEEP_STUBS);

        GeneralUtilMethods generalUtilMethods = new GeneralUtilMethods(context);
        when(context.getResources().getInteger(android.R.integer.config_shortAnimTime)).thenReturn(200);

        generalUtilMethods.showProgress(true, view, progressBar);
        verify(view).setVisibility(View.GONE);
        verify(progressBar).setVisibility(View.VISIBLE);
    }


    @Before
    public void prepareData(){

    }
    @Test
    public void resetToken_isDataReset(){
        Context mockContext = mock(Context.class, RETURNS_DEEP_STUBS);
        SharedPreferences.Editor fakeEditor = mock(SharedPreferences.Editor.class);
        GeneralUtilMethods generalUtilMethods = new GeneralUtilMethods(mockContext);

        when(mockContext.getSharedPreferences(anyString(),anyInt()).edit()).thenReturn(fakeEditor);
        generalUtilMethods.ResetToken();

        verify(fakeEditor).putString(eq(GeneralConstants.TOKEN), isNull(String.class));
        verify(fakeEditor).putBoolean(eq(GeneralConstants.AUTHENTICATED),eq(false));
        verify(fakeEditor).apply();
    }

    /* this is used to set Build.VERSION.SDK_INT while local unit testing */
    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}














