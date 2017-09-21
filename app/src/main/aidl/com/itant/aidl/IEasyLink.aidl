package com.itant.aidl;

// 一定要显式导入!!!!!!!!!!!!!!
import com.itant.aidl.Book;
interface IEasyLink {
    void testEasyLinkAIDL(String text);

    void anotherMethod(String text, in Book book);
}
