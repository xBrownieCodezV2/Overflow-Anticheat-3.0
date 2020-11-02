package me.jumba.overflow.util.minecraft.vector.util;


import java.nio.FloatBuffer;

public interface ReadableVector {
    float length();

    float lengthSquared();

    Vector store(FloatBuffer var1);
}
