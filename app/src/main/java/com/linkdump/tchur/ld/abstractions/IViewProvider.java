package com.linkdump.tchur.ld.abstractions;

import java.lang.reflect.Type;
import android.view.View;

public interface IViewProvider
{
     View getViewByUiName(String moniker);
     View getViewByType(Type type);
}
