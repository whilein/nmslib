/*
 *    Copyright 2021 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nmslib.api.nms;

import nmslib.api.annotation.FieldGenerated;

/**
 * @author whilein
 */
public interface Entity {

    @FieldGenerated
    Entity getPassenger();

    @FieldGenerated
    void setPassenger(Entity entity);

    @FieldGenerated
    Entity getVehicle();

    @FieldGenerated
    void setVehicle(Entity entity);

    int getId();

    @FieldGenerated
    int getTicksLived();

    @FieldGenerated
    void setTicksLived(int ticksLived);

    @FieldGenerated
    int getMaxFireTicks();

    @FieldGenerated
    void setMaxFireTicks(int maxFireTicks);

    @FieldGenerated
    int getFireTicks();

    @FieldGenerated
    void setFireTicks(int fireTicks);

    @FieldGenerated
    float getWidth();

    @FieldGenerated
    void setWidth(float width);

    @FieldGenerated
    float getLength();

    @FieldGenerated
    void setLength(float length);

    @FieldGenerated
    double getLastX();

    @FieldGenerated
    double getLastY();

    @FieldGenerated
    double getLastZ();

    @FieldGenerated
    double getLocX();

    @FieldGenerated
    double getLocY();

    @FieldGenerated
    double getLocZ();

    @FieldGenerated
    double getMotX();

    @FieldGenerated
    double getMotY();

    @FieldGenerated
    double getMotZ();

    @FieldGenerated
    float getYaw();

    @FieldGenerated
    float getPitch();

    @FieldGenerated
    float getLastYaw();

    @FieldGenerated
    float getLastPitch();

    @FieldGenerated
    void setLastX(double value);

    @FieldGenerated
    void setLastY(double value);

    @FieldGenerated
    void setLastZ(double value);

    @FieldGenerated
    void setLocX(double value);

    @FieldGenerated
    void setLocY(double value);

    @FieldGenerated
    void setLocZ(double value);

    @FieldGenerated
    void setMotX(double value);

    @FieldGenerated
    void setMotY(double value);

    @FieldGenerated
    void setMotZ(double value);

    @FieldGenerated
    void setYaw(float value);

    @FieldGenerated
    void setPitch(float value);

    @FieldGenerated
    void setLastYaw(float value);

    @FieldGenerated
    void setLastPitch(float value);
    
}
