package com.example.medjool.services;

import com.example.medjool.dto.NewPasswordDto;
import com.example.medjool.dto.UserDetailsDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserManagementService {

    /** * Fetch all the users.
     *
     * @return ResponseEntity with a list of all users
     */
    ResponseEntity<List<UserDetailsDto>> getAllUsers();

    ResponseEntity<UserDetailsDto> getUserById(Long id);

    /** * Activate a locked user account.
     *
     * @param id the id of the user account
     * @return ResponseEntity with the created user's details
     */
    ResponseEntity<Object> activateUserAccount(Long id);

    /** * Update user details.
     *
     * @param id the id of the user account
     * @param userDetailsDto the DTO containing updated user details
     * @return ResponseEntity with the updated user's details
     */
    ResponseEntity<Object> updateUserDetails(Long id, UserDetailsDto userDetailsDto);

    /** * Hold a user account, preventing further actions.
     *
     * @param id the id of the user account
     * @return ResponseEntity indicating the result of the operation
     */
    ResponseEntity<Object> holdUserAccount(Long id);

    /** * Delete a user account.
     *
     * @param id the id of the user account
     * @return ResponseEntity indicating the result of the operation
     */
    ResponseEntity<Object> deleteUserAccount(Long id);

    /** * Reset a user's password.
     *
     * @param id the id of the user account
     * @param newPassword the DTO containing the new password
     * @return ResponseEntity indicating the result of the operation
     */
    ResponseEntity<Object> resetUserPassword(Long id, NewPasswordDto newPassword);
}
