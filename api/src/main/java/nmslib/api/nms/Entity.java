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

/**
 * @author whilein
 */
public interface Entity {
    Entity getPassenger();
    void setPassenger(Entity entity);

    Entity getVehicle();
    void setVehicle(Entity entity);

    int getId();
    
    double getLastX();
    double getLastY();
    double getLastZ();

    double getLocX();
    double getLocY();
    double getLocZ();

    double getMotX();
    double getMotY();
    double getMotZ();

    float getYaw();
    float getPitch();
    
    float getLastYaw();
    float getLastPitch();

    void setLastX(double value);
    void setLastY(double value);
    void setLastZ(double value);

    void setLocX(double value);
    void setLocY(double value);
    void setLocZ(double value);

    void setMotX(double value);
    void setMotY(double value);
    void setMotZ(double value);

    void setYaw(float value);
    void setPitch(float value);

    void setLastYaw(float value);
    void setLastPitch(float value);
    
}
