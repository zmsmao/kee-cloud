package com.kee.common.core.exception.file;

import com.kee.common.core.exception.BaseException;

/**
 * 文件信息异常类
 * 
 * @author zms
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
